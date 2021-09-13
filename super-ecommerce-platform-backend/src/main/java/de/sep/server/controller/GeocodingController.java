package de.sep.server.controller;

import com.google.maps.model.LatLng;
import de.sep.domain.dto.GetHighestBidderDto;
import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.services.GeocodingService;
import de.sep.server.util.Logger;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class GeocodingController extends BaseController {

    Logger logger = new Logger(getClass().getCanonicalName());
    UserAdapter userAdapter;
    GeocodingService geocodingService;

    public GeocodingController(){
        try {
            geocodingService = new GeocodingService();
            userAdapter = new UserAdapter();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        ResponseEntity<Double> responseEntity = new ResponseEntity<>();
        Map<String,String> params = getQueryParameters(req.getQueryString());
        int userid1 = Integer.parseInt(params.get("userid1"));
        int userid2 = Integer.parseInt(params.get("userid2"));
        try {
            LatLng origin = new LatLng();
            LatLng destination = new LatLng();
            if (userid1 == userid2) {
                responseEntity.setData(0.0);
                responseEntity.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                ResultSet rs = userAdapter.getQuery("SELECT user_id, latitude, longitude From user Where user_id = '" + userid1 + "'" + "OR user_id = '"+ userid2 + "'");
                if (rs.next()) {
                    origin.lat = Double.parseDouble(rs.getString("latitude"));
                    origin.lng = Double.parseDouble(rs.getString("longitude"));
                    if (rs.next()) {
                        destination.lat = Double.parseDouble(rs.getString("latitude"));
                        destination.lng = Double.parseDouble(rs.getString("longitude"));
                        double distance = geocodingService.getDistance(origin, destination);
                        responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
                        responseEntity.setStatus(HttpServletResponse.SC_OK);
                        responseEntity.setData(distance);

                    }
                    else {
                        responseEntity.setStatus(SC_NOT_FOUND);
                        responseEntity.setMessage(ResponseMessages.NOT_FOUND.getResponse());
                    }
                }
                else {
                    responseEntity.setStatus(SC_NOT_FOUND);
                    responseEntity.setMessage(ResponseMessages.NOT_FOUND.getResponse());
                }
            }

        }
        catch (SQLException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        }
        try {
            this.sendResponse(resp, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
            // Please not delete
/*        Map<String,String> params = getQueryParameters(req.getQueryString());
        int userid = Integer.parseInt(params.get("userid"));
        ResponseEntity<HashMap<Integer, String>> responseEntity = new ResponseEntity<>();

        try {
            ResultSet rs1 = userAdapter.getQuery("SELECT user_id, latitude, longitude From user Where user_id != '" + userid + "'");
            ResultSet rs2 = userAdapter.getQuery("SELECT user_id, latitude, longitude From user Where user_id = '" + userid + "'");

            ArrayList<String> originsArrayList = new ArrayList<>();
            ArrayList<String> destinationsArrayList = new ArrayList<>();

            if (rs1.next()) {
                do {
                    originsArrayList.add(rs1.getString("latitude") +","+ rs1.getString("longitude"));
                } while (rs1.next());
            } else {
                responseEntity.setStatus(SC_NOT_FOUND);
                responseEntity.setMessage(ResponseMessages.NOT_FOUND.getResponse());
            }
            if (rs2.next()) {
                do {
                    originsArrayList.add(rs2.getString("latitude") +","+ rs2.getString("longitude"));
                } while (rs2.next());
            } else {
                responseEntity.setStatus(SC_NOT_FOUND);
                responseEntity.setMessage(ResponseMessages.NOT_FOUND.getResponse());
            }
            String [] origins = new String[originsArrayList.size()];
            String [] destinations = new String[destinationsArrayList.size()];

            for (int i=0;i<origins.length;i++){
                origins [i] = originsArrayList.get(i);
            }
            for (int i=0;i<destinations.length;i++){
                destinations [i] = destinationsArrayList.get(i);
            }
            ArrayList<Double> distances = geocodingService.getDistances(origins, destinations);





        } catch (SQLException e) {
        responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseEntity.setMessage(e.getMessage());
    } catch (NumberFormatException e) {
        responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        responseEntity.setMessage(e.getMessage());
    }
    }



*/