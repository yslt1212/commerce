package de.sep.server.controller;

import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.dto.GetProductDto;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class LastViewedController extends BaseController {

    UserAdapter userAdapter;

    public LastViewedController(){
        try {
            userAdapter = new UserAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String> queryParameters = getQueryParameters(req.getQueryString());
        ResponseEntity responseEntity = new ResponseEntity<>();

        try {
            userAdapter = new UserAdapter();
            HttpSession session = req.getSession();

            authenticate(session);
            int amount = Integer.parseInt(queryParameters.get("amount"));
            int userid = Integer.parseInt(session.getAttribute("userid").toString());

            List<GetProductDto> productList= userAdapter.getLastViewedProducts(userid, amount);

            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setData(productList);

        } catch (SQLException | InternalServerErrorException | NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        }

        sendResponse(resp, responseEntity);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String> queryParameters = getQueryParameters(req.getQueryString());
        ResponseEntity responseEntity = new ResponseEntity<>();

        try {
            userAdapter = new UserAdapter();
            HttpSession session = req.getSession();

            authenticate(session);
            int productid = Integer.parseInt(queryParameters.get("productid"));
            int userid = Integer.parseInt(session.getAttribute("userid").toString());

            userAdapter.lookAtProduct(userid, productid);

            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());

        } catch (SQLException | InternalServerErrorException | NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        } catch (NotFoundException e) {
            responseEntity.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        }

        sendResponse(resp, responseEntity);
    }
}
