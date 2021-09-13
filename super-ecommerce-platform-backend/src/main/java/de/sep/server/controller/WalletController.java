package de.sep.server.controller;

import de.sep.server.adapter.UserAdapter;
import de.sep.server.controller.BaseController;
import de.sep.domain.entity.UserEntity;
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
import java.util.Map;

public class WalletController extends BaseController {

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> queryParameters = getQueryParameters(req.getQueryString());
        ResponseEntity responseEntity = new ResponseEntity<>();

        try {
            UserAdapter userAdapter = new UserAdapter();
            HttpSession session = req.getSession();

            authenticateWithType(session, 0);
            Double amount = Double.parseDouble(queryParameters.get("amount"));
            int userid = Integer.parseInt(session.getAttribute("userid").toString());
            UserEntity user = userAdapter.getUserById(userid);

            Double oldBalance = user.getBalance();

            Double supposedNewBalance = amount + oldBalance;

            userAdapter.setBalance(userid, supposedNewBalance);

            user = userAdapter.getUserById(userid);

            Double newBalance = user.getBalance();

            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
            responseEntity.setData(newBalance);

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
