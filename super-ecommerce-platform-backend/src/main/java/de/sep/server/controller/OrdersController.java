package de.sep.server.controller;

import de.sep.server.adapter.ProductAdapter;
import de.sep.server.controller.BaseController;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static javax.servlet.http.HttpServletResponse.*;

public class OrdersController extends BaseController {

    ProductAdapter productAdapter;

    public OrdersController(){
        try {
            productAdapter = new ProductAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {

            authenticate(request.getSession());
            int userid = (int) request.getSession().getAttribute("userid");

            responseEntity.setData(productAdapter.getOrders(userid));
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(SC_OK);

        } catch (UnauthorizedException e){
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

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity responseEntity = new ResponseEntity();

        try {
            authenticate(request.getSession());
            int order_id = Integer.parseInt(request.getParameter("order_id"));
            productAdapter.deleteOrder(order_id);
            responseEntity.setStatus(SC_OK);
        } catch (SQLException | UnauthorizedException exception) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(exception.getMessage());
            exception.printStackTrace();
        }
        try {
            sendResponse(response,responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
