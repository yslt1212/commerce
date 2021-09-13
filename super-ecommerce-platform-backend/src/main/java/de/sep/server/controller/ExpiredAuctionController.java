package de.sep.server.controller;

import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.GetAuctionDto;
import de.sep.server.errors.NotFoundException;
import de.sep.server.util.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class ExpiredAuctionController extends BaseController {

    AuctionAdapter auctionAdapter;

    public ExpiredAuctionController(){
        try {
            auctionAdapter = new AuctionAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            auctionAdapter = new AuctionAdapter();
            if(request.getParameter("auctionid") != null){
                int auctionid = Integer.parseInt(request.getParameter("auctionid"));

                GetAuctionDto auction = auctionAdapter.getAuctionById(auctionid);

                responseEntity.setData(auction);
            } else {
                List<GetAuctionDto> auctions = auctionAdapter.getAllExpiredAuctions();
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
