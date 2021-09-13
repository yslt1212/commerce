package de.sep.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.sep.server.adapter.ProductAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.GetProductDto;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.Logger;
import de.sep.server.util.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

public class SimiliarProductController extends BaseController {

    ProductAdapter productAdapter;



    Gson gson;

    public SimiliarProductController(){
        try {
            productAdapter = new ProductAdapter();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Logger logger = new Logger(getClass().getCanonicalName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            authenticate(request.getSession());

            if(request.getParameter("productid") != null){
                int productId = Integer.parseInt(request.getParameter("productid"));

                List<GetProductDto> products= productAdapter.getSimiliarProducts(productId);

                responseEntity.setData(products);
            }
            else{
                responseEntity.setStatus(SC_BAD_REQUEST);
                responseEntity.setMessage("No ProductId");
            }
        } catch (NotFoundException | NumberFormatException e) {
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

