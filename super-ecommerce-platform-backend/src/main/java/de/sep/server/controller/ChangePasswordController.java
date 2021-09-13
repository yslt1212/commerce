package de.sep.server.controller;

import com.google.gson.Gson;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.ChangePasswordDto;
import de.sep.server.errors.BadRequestException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class ChangePasswordController extends BaseController {

    UserAdapter userAdapter;

    public ChangePasswordController(){
        try {
            userAdapter = new UserAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String> queryParameters = getQueryParameters(req.getQueryString());
        ResponseEntity responseEntity = new ResponseEntity<>();

        try {
            Gson gson = new Gson();
            int userid = Integer.parseInt(queryParameters.get("userid"));
            String bodyData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            ChangePasswordDto changePasswordDto = gson.fromJson(bodyData, ChangePasswordDto.class);
            HttpSession session = req.getSession();

            // Authenticating the session
            authenticate(session, userid);

            userAdapter.changePassword(userid, changePasswordDto);

            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(HttpServletResponse.SC_OK);

        }catch (InternalServerErrorException | IOException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (NotFoundException e) {
            responseEntity.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        } catch (BadRequestException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        }

        try {
            sendResponse(resp, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
