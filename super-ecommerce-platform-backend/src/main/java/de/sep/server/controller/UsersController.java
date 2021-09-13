package de.sep.server.controller;

import com.google.gson.Gson;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.GetUserDto;
import de.sep.domain.dto.UpdateUserDto;
import de.sep.domain.entity.UserEntity;
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

// /users
public class UsersController extends BaseController {
    UserAdapter userAdapter;

    public UsersController() {
        try {
            userAdapter = new UserAdapter();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        ResponseEntity<GetUserDto> responseEntity = new ResponseEntity<>();

        try {
            HttpSession session = req.getSession();


            // Authenticating the session
            authenticate(session);



            UserEntity user = userAdapter.getUserById((int) session.getAttribute("userid"));
            GetUserDto getUserDto;
            getUserDto = new GetUserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getStreet(),
                    user.getPostcode(),
                    user.getCity(),
                    user.getBalance(),
                    user.getType(),
                    user.getImg(),
                    user.getWantsTFA()
            );

            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setData(getUserDto);

        }catch (InternalServerErrorException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        } catch (NotFoundException e) {
            responseEntity.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        }

        try {
            sendResponse(resp, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {

        ResponseEntity<UpdateUserDto> responseEntity = new ResponseEntity<>();

        try {
            Map<String, String> queryParameters = getQueryParameters(req.getQueryString());
            Gson gson = new Gson();
            int userid = Integer.parseInt(queryParameters.get("userid"));

            String bodyData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            System.out.println(bodyData);
            UpdateUserDto updateUserDto = gson.fromJson(bodyData, UpdateUserDto.class);
            HttpSession session = req.getSession();

            // Authenticating the session
            authenticate(session, userid);

            userAdapter.updateUser(userid, updateUserDto);

            UserEntity user = userAdapter.getUserById(userid);

            System.out.println(user);

            UpdateUserDto updatedUser = new UpdateUserDto(
                    user.getUsername(),
                    user.getEmail(),
                    user.getStreet(),
                    user.getPostcode(),
                    user.getCity(),
                    user.getImg(),
                    user.getWantsTFA());

            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setData(updatedUser);

        }catch (InternalServerErrorException| IOException | NumberFormatException e) {
            e.printStackTrace();
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

