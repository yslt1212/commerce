package de.sep.server.controller;

import com.google.gson.reflect.TypeToken;
import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.AddAuctionDto;
import de.sep.domain.dto.GetAuctionDto;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

public class AuctionController extends BaseController {

    AuctionAdapter auctionAdapter;

    public AuctionController(){
        try {
            auctionAdapter = new AuctionAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response){

        ResponseEntity responseEntity = new ResponseEntity();
        List<AddAuctionDto> auctions = new ArrayList<>();


        try {

            authenticate(request.getSession());
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Type requestBodyType;
            if(body.trim().startsWith("[")) {
                requestBodyType = new TypeToken<List<AddAuctionDto>>() {}.getType();
                auctions = gson.fromJson(body,requestBodyType);
            }else {
                requestBodyType = new TypeToken<AddAuctionDto>() {}.getType();
                auctions.add(gson.fromJson(body,requestBodyType));
            }

            auctionAdapter.addAuctions(auctions);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(HttpServletResponse.SC_OK);
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            e.printStackTrace();
        } catch (IOException e) {
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
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            if(request.getParameter("auctionid") != null){
                int auctionid = Integer.parseInt(request.getParameter("auctionid"));

                GetAuctionDto auction = auctionAdapter.getAuctionById(auctionid);

                responseEntity.setData(auction);
            } else {
                List<GetAuctionDto> auctions = auctionAdapter.getAllAuctions();
                if(!auctions.isEmpty()) {
                    responseEntity.setStatus(SC_OK);
                }else{
                    responseEntity.setStatus(SC_NO_CONTENT);
                }
                responseEntity.setData(auctions);
            }
        } catch (NotFoundException | NumberFormatException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
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
