package de.sep.server.controller;

import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.controller.BaseController;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class AuctionImgController extends BaseController {

    AuctionAdapter auctionAdapter;

    public AuctionImgController(){
        try {
            auctionAdapter = new AuctionAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            int auctionId = Integer.parseInt(request.getParameter("auctionid"));
            auctionAdapter = new AuctionAdapter();

            responseEntity.setData(auctionAdapter.getImg(auctionId));
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(SC_OK);

        } catch (NotFoundException e) {
            responseEntity.setStatus(SC_NOT_FOUND);
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
