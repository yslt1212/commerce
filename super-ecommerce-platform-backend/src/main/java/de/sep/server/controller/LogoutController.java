package de.sep.server.controller;

import de.sep.server.controller.BaseController;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class LogoutController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        ResponseEntity responseEntity = new ResponseEntity();

        try {

            Map<String, String> queryparameter = getQueryParameters(req.getQueryString());
            int userid = Integer.parseInt(queryparameter.get("userid"));

            HttpSession session = req.getSession();
            authenticate(session, userid);

            session.removeAttribute("authentication");
            session.removeAttribute("userid");

            System.out.println("Logged out user :" + userid);
            responseEntity.setStatus(HttpServletResponse.SC_OK);
            responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        } catch (NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getLocalizedMessage());
        }

        try {
            sendResponse(resp, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
