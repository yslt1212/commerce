package de.sep.server.controller;

import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.GetSellerDto;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.util.Logger;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import java.util.Map;


public class SellerController extends BaseController {
Logger logger = new Logger(getClass().getCanonicalName());
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        ResponseEntity<GetSellerDto> responseEntity = new ResponseEntity<>();
        Map<String,String> params = getQueryParameters(req.getQueryString());
        try {
            UserAdapter userAdapter = new UserAdapter();
            int userid = Integer.parseInt(params.get("userid"));
            UserEntity user = userAdapter.getUserById(userid);
            String companyName = "";
            try {
                 companyName= userAdapter.getCompanynameByID(userid);
            } catch (NotFoundException e) {
                //e.printStackTrace();
            }





            GetSellerDto getSellerDto  = new GetSellerDto(
                    userid,
                    user.getUsername(),
                    user.getEmail(),
                    companyName);

            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setData(getSellerDto);

        }catch (SQLException | InternalServerErrorException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        } catch (NotFoundException e) {
            responseEntity.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        }

        try {
            sendResponse(resp, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
