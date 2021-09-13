package de.sep.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.sep.domain.dto.GetUserGiftDto;
import de.sep.domain.dto.UpdateVoucherDto;
import de.sep.server.adapter.RatingAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.AddRatingDto;
import de.sep.domain.dto.GetRatingDto;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

public class SendGiftToController extends BaseController {

    UserAdapter userAdapter;

    Gson gson;

    public SendGiftToController(){
        try {
            userAdapter = new UserAdapter();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response){

        ResponseEntity responseEntity = new ResponseEntity();


        try {
            Type requestBodyType = new TypeToken<UpdateVoucherDto>() {}.getType();
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            userAdapter.changeVoucherOwnership(gson.fromJson(body,requestBodyType));



        } catch (IOException | SQLException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
            e.printStackTrace();
        }
        try {
            sendResponse(response, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        System.out.println("Irgendwas");
        ResponseEntity responseEntity = new ResponseEntity();
        try {

            authenticate(request.getSession());

            List<GetUserGiftDto> getUserGiftDtoList = userAdapter.getUserGifts(Integer.parseInt(request.getParameter("userId")));
            if(getUserGiftDtoList.size() > 0) {
                responseEntity.setData(getUserGiftDtoList);
                responseEntity.setStatus(SC_OK);
            } else {

            }
        } catch (SQLException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
            e.printStackTrace();
        }  catch(Exception e){
            responseEntity.setMessage("");
            responseEntity.setStatus(404);
        }
        try {
            sendResponse(response, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
