package de.sep.server.controller;

import de.sep.server.adapter.ProductAdapter;
import de.sep.server.controller.BaseController;
import de.sep.server.util.ResponseEntity;
import de.sep.server.util.ResponseMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;

public class ProductHistoryController extends BaseController {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp){

        ResponseEntity<Double> responseEntity = new ResponseEntity();
        Map<String,String> params = getQueryParameters(req.getQueryString());
        double oldprice;
        try {
            ProductAdapter productAdapter = new ProductAdapter();
            int offerid = Integer.parseInt(params.get("offerid"));
            ResultSet rs = productAdapter.getQuery("SELECT old_price FROM offer, offer_history, product_history WHERE offer.offer_id = offer_history.offer_id AND offer_history.offer_history_id = product_history.offer_history_id AND offer.offer_id = '"+ offerid +"' ORDER BY product_history.time_stamp DESC LIMIT 1");
            if (rs.next()) {
                oldprice = rs.getDouble("old_price");
                responseEntity.setMessage(ResponseMessages.SUCCESSFUL.getResponse());
                responseEntity.setStatus(HttpServletResponse.SC_OK);
                responseEntity.setData(oldprice);
            }
            else {
                responseEntity.setStatus(SC_NOT_FOUND);
                responseEntity.setMessage(ResponseMessages.NOT_FOUND.getResponse());
            }
        }catch (SQLException e) {
            responseEntity.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(e.getMessage());
        } catch (NumberFormatException e) {
            responseEntity.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseEntity.setMessage(e.getMessage());
        }
        try {
            sendResponse(resp, responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
