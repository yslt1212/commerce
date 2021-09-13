package de.sep.server.controller;

import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.adapter.ProductAdapter;
import de.sep.server.controller.BaseController;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class MyProductController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResponseEntity responseEntity = new ResponseEntity();
        try {

            authenticate(request.getSession());
            int userid = (int) request.getSession().getAttribute("userid");
            ProductAdapter productAdapter = new ProductAdapter();

            responseEntity.setData(productAdapter.getMyProducts(userid));
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(SC_OK);

        } catch (NotFoundException e) {
            responseEntity.setStatus(SC_NOT_FOUND);
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
