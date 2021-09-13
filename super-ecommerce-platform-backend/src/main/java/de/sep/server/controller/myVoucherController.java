package de.sep.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.sep.domain.dto.GetVoucherDto;
import de.sep.domain.dto.VoucherDto;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Data
@AllArgsConstructor

public class myVoucherController extends BaseController{


    UserAdapter userAdapter;
    Gson gson;

    public myVoucherController() {
        try {
            userAdapter = new UserAdapter();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response){

        ResponseEntity responseEntity = new ResponseEntity();

        try {
            authenticate(request.getSession());

            List<GetVoucherDto> svdto = userAdapter.showVoucher(Integer.parseInt(request.getParameter("user_id")));
            responseEntity.setData(svdto);
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
            int userid = Integer.parseInt(queryParameters.get("user_id"));
            int value = Integer.parseInt(queryParameters.get("value"));
            String code = queryParameters.get("code");

            VoucherDto voucher = new VoucherDto();
            voucher.setUser_id(userid);
            voucher.setValue(value);
            voucher.setCode(code);

            userAdapter.addVoucher(voucher);
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
