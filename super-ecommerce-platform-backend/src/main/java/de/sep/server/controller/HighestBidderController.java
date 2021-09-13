package de.sep.server.controller;

import de.sep.domain.dto.GetHighestBidderDto;
import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.controller.BaseController;
import de.sep.server.errors.NotFoundException;
import de.sep.server.util.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class HighestBidderController extends BaseController {

    AuctionAdapter auctionAdapter;

    public HighestBidderController(){
        try {
            auctionAdapter = new AuctionAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
                List<GetHighestBidderDto> hb = auctionAdapter.getHighestBidder();
                if(!hb.isEmpty()) {
                    responseEntity.setStatus(SC_OK);
                }else{
                    responseEntity.setStatus(SC_NO_CONTENT);
                }
                responseEntity.setData(hb);

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
