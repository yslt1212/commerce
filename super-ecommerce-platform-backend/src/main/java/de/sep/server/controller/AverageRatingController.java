package de.sep.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.sep.server.adapter.RatingAdapter;
import de.sep.server.controller.BaseController;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static javax.servlet.http.HttpServletResponse.*;

public class AverageRatingController extends BaseController {


    RatingAdapter ratingAdapter;


    Gson gson;

    public AverageRatingController(){
        try {
            ratingAdapter= new RatingAdapter();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            authenticate(request.getSession());

            if(request.getParameter("userId") != null){
                int userId = Integer.parseInt(request.getParameter("userId"));

               float averageRating= ratingAdapter.getAverageRating(userId);

                responseEntity.setData(averageRating);
            }
            else{
                responseEntity.setStatus(SC_BAD_REQUEST);
                responseEntity.setMessage("No ProductId");
            }
        } catch (NumberFormatException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        }catch (UnauthorizedException e){
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        }catch (Exception e){
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

