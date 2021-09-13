package de.sep.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.sep.domain.dto.GetVoucherDto;
import de.sep.server.adapter.RatingAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.AddRatingDto;
import de.sep.domain.dto.GetRatingDto;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;

import com.google.gson.reflect.TypeToken;
import de.sep.domain.dto.AddAuctionDto;
import de.sep.domain.dto.VoucherDto;
import de.sep.server.adapter.ProductAdapter;
import de.sep.server.adapter.UserAdapter;
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
import java.util.Map;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

public class VoucherController extends BaseController {

    UserAdapter userAdapter;

    Gson gson;

    public VoucherController() {
        try {
            userAdapter = new UserAdapter();
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

            GetVoucherDto gvdto = userAdapter.getVoucher(request.getParameter("code"));

            responseEntity.setData(gvdto);

            responseEntity.setStatus(SC_OK);

        } catch (SQLException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());

            e.printStackTrace();
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());

            e.printStackTrace();
        }
        try {
            sendResponse(response, responseEntity);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity responseEntity = new ResponseEntity();

        try {

            authenticate(request.getSession());
            Map<String, String> queryParameters = getQueryParameters(request.getQueryString());
            String code = queryParameters.get("code");

            userAdapter.deactivateVoucher(code);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setStatus(HttpServletResponse.SC_OK);
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            e.printStackTrace();
        } catch (SQLException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            System.out.println(request.getParameter("code"));
        }

            try {
                sendResponse(response, responseEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }
