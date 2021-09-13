package de.sep.server.controller;

import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.GetAuctionDto;
import de.sep.server.errors.BadRequestException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.Logger;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class BidController extends BaseController {
    Logger logger = new Logger(getClass().getCanonicalName());

    AuctionAdapter auctionAdapter;
    UserAdapter userAdapter;

    public BidController(){
        try {
            auctionAdapter = new AuctionAdapter();
            userAdapter = new UserAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String> queryParameters = getQueryParameters(req.getQueryString());
        ResponseEntity responseEntity = new ResponseEntity<>();

        try {

            HttpSession session = req.getSession();

            authenticate(session);
            int auctionid = Integer.parseInt(queryParameters.get("auctionid"));
            double bidAmount = Double.parseDouble(queryParameters.get("amount"));
            int userid = Integer.parseInt(session.getAttribute("userid").toString());

            GetAuctionDto auction = auctionAdapter.getAuctionById(auctionid);

           if(auction.getBidAmount() >= bidAmount || auction.getStartPrice() >= bidAmount) {
                throw new BadRequestException("Zu wenig geboten");
           }

           userAdapter.bidOnAuction(userid,bidAmount, auctionid);


            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());

        } catch (UnauthorizedException e) {
            responseEntity.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        } catch (NotFoundException e) {
            responseEntity.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        } catch (BadRequestException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        }  catch (InternalServerErrorException | NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (Exception e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        }

        sendResponse(resp, responseEntity);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {

            authenticate(request.getSession());
            int userid = (int) request.getSession().getAttribute("userid");

            responseEntity.setData(userAdapter.getMyBids(userid));
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(SC_OK);

        } catch (NotFoundException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        }catch (UnauthorizedException e){
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        }catch (Exception  e){
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
