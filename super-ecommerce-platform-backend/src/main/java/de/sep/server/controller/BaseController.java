package de.sep.server.controller;

import com.google.gson.Gson;
import de.sep.server.adapter.UserAdapter;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.*;
import de.sep.server.util.Logger;
import de.sep.server.util.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static javax.servlet.http.HttpServletResponse.*;

public abstract class BaseController extends HttpServlet {
    Logger logger = new Logger(getClass().getCanonicalName());
    protected Gson gson = new Gson();
    // Only looks if the user is logged in
    public void authenticate(HttpSession session) throws UnauthorizedException {
        try {
            if(!session.getAttribute("authentication").toString().equals("true")) {
                throw new UnauthorizedException();
            }
        } catch (NullPointerException e) {
            throw new UnauthorizedException();
        }


    }

    // Authenticates if the user is logged in and if the given userid fits to the id of the user
    public void authenticate(HttpSession session, int userid) throws UnauthorizedException {
        try {
            if(!session.getAttribute("authentication").toString().equals("true")) {
                throw new UnauthorizedException();
            } else {
                if (!session.getAttribute("userid").toString().equals(Integer.toString(userid))) {
                    throw new UnauthorizedException();
                }
            }
        } catch (NullPointerException e) {
            throw new UnauthorizedException();
        }
    }

    public void authenticateWithType(HttpSession session, int usertype) throws UnauthorizedException {
        authenticate(session);
        try {
            UserAdapter userAdapter = new UserAdapter();
            int userid = Integer.parseInt(session.getAttribute("userid").toString());
            UserEntity user = userAdapter.getUserById(userid);

            if (user.getType() != usertype) {
                    throw new UnauthorizedException("Invalid User Role");
            }
        } catch (SQLException |InternalServerErrorException e) {
            throw new UnauthorizedException(e.getMessage());
        } catch (NotFoundException e) {
            throw new UnauthorizedException(e.getMessage());
        }


    }

    public void authenticateWithType(HttpSession session,int userid, int usertype) throws UnauthorizedException {
        authenticate(session, userid);
        try {
            UserAdapter userAdapter = new UserAdapter();
            UserEntity user = userAdapter.getUserById(userid);

            if (user.getType() != usertype) {
                throw new UnauthorizedException("Invalid User Role");
            }
        } catch (SQLException | InternalServerErrorException e) {
            throw new UnauthorizedException(e.getMessage());
        } catch (NotFoundException e) {
            throw new UnauthorizedException(e.getMessage());
        }


    }




    public Map<String, String> getQueryParameters(String parameter) {
        // Creating a Map to Store the param pairs in
        Map<String, String> parameterMap = new HashMap<>();

        if (parameter != null) {
            // Saving the firstParts in an String array
            String[] firstPart = parameter.split("&");

            // Getting the second part
            for (String s : firstPart) {
                // Storing parameter pair into an String array
                // 0 = name of pair
                // 1 = value
                String[] parameters = s.split("=");

                // Adding values to Map
                parameterMap.put(parameters[0], parameters[1]);
            }

            // Giving stuff back
        }

        return parameterMap;
    }

    // Function to return a Response
    public void sendResponse(HttpServletResponse resp, ResponseEntity responseEntity) throws IOException {
        // Creating PrintWriter
        PrintWriter out = resp.getWriter();
        resp.setStatus(responseEntity.getStatus());
        resp.setContentType("application/json");
        // writing to the client
        out.println(gson.toJson(responseEntity));
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }


    protected ResponseEntity getExceptionResponseEntity(Exception e){
        ResponseEntity responseEntity = new ResponseEntity<>();
        if(e instanceof BadRequestException) {
            responseEntity.setStatus(SC_BAD_REQUEST);
        }else if(e instanceof ForbiddenException) {
            responseEntity.setStatus(SC_FORBIDDEN);
        }else if(e instanceof NotFoundException) {
            responseEntity.setStatus(SC_NOT_FOUND);
        }else if(e instanceof UnauthorizedException) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
        }else{
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
        responseEntity.setData(null);
        responseEntity.setMessage(e.getMessage());
        return responseEntity;
    }
}
