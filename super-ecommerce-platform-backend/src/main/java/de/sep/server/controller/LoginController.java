package de.sep.server.controller;

import com.google.gson.Gson;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.LoggedUserDto;
import de.sep.domain.dto.LoginUserDto;

import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.ForbiddenException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginController extends BaseController {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String bodyData = null;
        // Getting BodyData
        Gson g = new Gson();
        // Creating ResponseEntity
        ResponseEntity<LoggedUserDto> responseEntity = new ResponseEntity<>();
        try {
            UserAdapter userAdapter = new UserAdapter();
            bodyData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            // Parsing BodyData to Dto
            LoginUserDto lu = g.fromJson(bodyData, LoginUserDto.class);


            // Registering the User
            userAdapter.loginUser(lu.getUsername(), lu.getPassword());
            HttpSession session = req.getSession();
            UserEntity user = userAdapter.getByName(lu.getUsername());
            session.setAttribute("authentication", "true");
            session.setAttribute("userid", user.getId());
            LoggedUserDto loggedUserDto = new LoggedUserDto(user.getId());
            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setData(loggedUserDto);


        } catch (InternalServerErrorException | IOException | SQLException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (ForbiddenException e) {
            responseEntity.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
