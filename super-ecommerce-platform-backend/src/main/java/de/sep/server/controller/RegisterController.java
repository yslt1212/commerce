package de.sep.server.controller;

import com.google.gson.Gson;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.RegisterUserDto;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.BadRequestException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterController extends BaseController {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String bodyData = null;
        // Getting BodyData
        Gson g = new Gson();
        // Creating ResponseEntity
        ResponseEntity<Integer> responseEntity = new ResponseEntity<>();
        try {
            UserAdapter userAdapter = new UserAdapter();
            bodyData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            // Parsing BodyData to Dto
            RegisterUserDto rg = g.fromJson(bodyData, RegisterUserDto.class);

            // Registering the User
            userAdapter.registerUser(rg);

            UserEntity user = userAdapter.getByName(rg.getUsername());
            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setData(user.getId());

        } catch (BadRequestException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());

        } catch (InternalServerErrorException | IOException | SQLException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        try {
            sendResponse(resp, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
