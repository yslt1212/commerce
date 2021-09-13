package de.sep.server.controller;

import com.google.maps.model.LatLng;
import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.util.Logger;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class CoordinatesController extends BaseController{

    Logger logger = new Logger(getClass().getCanonicalName());
    AuctionAdapter auctionAdapter;

    public CoordinatesController (){
        try {
            auctionAdapter = new AuctionAdapter();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        ResponseEntity<LatLng> responseEntity = new ResponseEntity<>();
        Map<String, String> params = getQueryParameters(req.getQueryString());
        int sellerid = Integer.parseInt(params.get("sellerid"));
        try {
            LatLng coordinates = new LatLng();

            ResultSet rs = auctionAdapter.getQuery("SELECT latitude, longitude FROM user WHERE user_id = '" + sellerid + "'");
            if (rs.next()) {
                coordinates.lat = Double.parseDouble(rs.getString("latitude"));
                coordinates.lng = Double.parseDouble(rs.getString("longitude"));
                responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
                responseEntity.setStatus(HttpServletResponse.SC_OK);
                responseEntity.setData(coordinates);
                this.sendResponse(resp, responseEntity);
            } else {
                responseEntity.setStatus(SC_NOT_FOUND);
                responseEntity.setMessage(ResponseMessages.NOT_FOUND.getResponse());
            }
        } catch (SQLException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
