package de.sep.server.controller.doubleAuth;

import de.sep.domain.entity.UserEntity;
import de.sep.server.adapter.DoubleAuthAdapter;
import de.sep.server.adapter.EmailAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.server.errors.BadRequestException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class DoubleAuthController extends BaseController {

    DoubleAuthAdapter doubleAuthAdapter = new DoubleAuthAdapter();
    EmailAdapter emailAdapter = new EmailAdapter();
    UserAdapter userAdapter = new UserAdapter();

    public DoubleAuthController() throws SQLException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            Map<String, String> queryParameters = getQueryParameters(request.getQueryString());
            int type = 0;
            int doubleAuthCode;
            String email = queryParameters.get("email");
            if(email != null) {
                // In this case an email is given so we can associate it with the given email
                doubleAuthCode = doubleAuthAdapter.getCode(email, type);
            } else {
                // In this case an tel number is given so we can associate it with that
                String tel = queryParameters.get("tel");
                if(tel != null) {
                    type = 1;
                    doubleAuthCode = doubleAuthAdapter.getCode(tel, type);
                }else {
                    // In this case none is given and we got an actual error
                    throw new BadRequestException("You messed something up");
                }

            }

            int code = Integer.parseInt(queryParameters.get("code"));

            // Has to return if code was same
            boolean success = false;

            if(doubleAuthCode == code) {
                success = true;
                doubleAuthAdapter.deactivateCode(code, type);
            }

            responseEntity.setData(success);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(SC_OK);

        }catch (BadRequestException e) {
            responseEntity.setData(false);
            responseEntity.setStatus(SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        }
        catch (NotFoundException e) {
            responseEntity.setData(false);
            responseEntity.setStatus(SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        }catch (Exception  e){
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        }finally {
            try {
                sendResponse(response, responseEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            Map<String, String> queryParameters = getQueryParameters(request.getQueryString());

            int double_auth_code_id;
            String email = queryParameters.get("email");

            if(email == null) {
                // If that email is not given something went wrong
                throw new BadRequestException("For some reason E-Mail is not set");
            }
            double_auth_code_id = doubleAuthAdapter.createCode(email, 0);

            System.out.println(double_auth_code_id);

            int code = doubleAuthAdapter.getCodeById(double_auth_code_id);


            emailAdapter.sendMail("Two - Factor - Authentication", "Your code is: " + code,email);


            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(SC_OK);

        } catch (NotFoundException e) {
            responseEntity.setStatus(SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());

        }catch (BadRequestException e) {
            responseEntity.setStatus(SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        }
        catch (Exception  e){
            e.printStackTrace();
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        }finally {
            try {
                sendResponse(response, responseEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
