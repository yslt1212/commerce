package de.sep.javafx.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import de.sep.domain.dto.GetHighestBidderDto;
import de.sep.domain.model.Auction;
import de.sep.domain.dto.AddAuctionDto;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.scene.image.Image;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuctionService {
    HttpClient client;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Logger logger = new Logger(getClass().getCanonicalName());

    public AuctionService(HttpClient client) {
        this.client = client;
    }

public ResponseEntity<LatLng> getCoordinates (int sellerid) throws Exception {
        client.start();
    ContentResponse res = client.GET("http://localhost:8080/auctions/coordinates?sellerid="+sellerid);
    Type responseEntityType = new TypeToken<ResponseEntity<LatLng>>() {}.getType();
    ResponseEntity<LatLng> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
    client.stop();
    return responseEntity;
}

    public ResponseEntity<List<GetHighestBidderDto>> getHighestBidder() throws Exception {
        client.start();
        ContentResponse res = client.GET("http://localhost:8080/auctions/highestbidder");
        Type responseEntityType = new TypeToken<ResponseEntity<List<GetHighestBidderDto>>>() {}.getType();
        ResponseEntity<List<GetHighestBidderDto>> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }


    public ResponseEntity<Auction> getAuctionById(int auctionid) throws Exception {
        client.start();

        ContentResponse res = client.GET("http://localhost:8080/auctions?auctionid="+auctionid);
        // Should return token? or User?
        Type responseEntityType = new TypeToken<ResponseEntity<Auction>>() {}.getType();
        ResponseEntity<Auction> responseEntity =
                gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<List<Auction>> getAllAuctions() throws Exception {
        client.start();

        ContentResponse res = client.GET("http://localhost:8080/auctions");
        Type responseEntityType = new TypeToken<ResponseEntity<List<Auction>>>() {}.getType();
        ResponseEntity<List<Auction>> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<List<Auction>> getAllExpiredAuctions() throws Exception {
        client.start();
        ContentResponse res = client.GET("http://localhost:8080/auctions/expired");
        Type responseEntityType = new TypeToken<ResponseEntity<List<Auction>>>() {}.getType();
        ResponseEntity<List<Auction>> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<List<Auction>> getMy() throws Exception {
        client.start();

        ContentResponse res = client.GET("http://localhost:8080/user/auctions/my");
        // Should return token? or User?
        Type responseEntityType = new TypeToken<ResponseEntity<List<Auction>>>() {}.getType();
        ResponseEntity<List<Auction>> responseEntity =
                gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<List<Auction>> getMyBid() throws Exception {
        client.start();
        ContentResponse res = client.GET("http://localhost:8080/user/auctions/bid");
        // Should return token? or User?
        Type responseEntityType = new TypeToken<ResponseEntity<List<Auction>>>() {}.getType();
        ResponseEntity<List<Auction>> responseEntity =
                gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<List<Auction>> getNotedAuctions() throws Exception {
        client.start();

        ContentResponse res = client.GET("http://localhost:8080/user/auctions/note");
        // Should return token? or User?
        Type responseEntityType = new TypeToken<ResponseEntity<List<Auction>>>() {}.getType();
        ResponseEntity<List<Auction>> responseEntity =
                gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity addAuction(AddAuctionDto addAuctionDto) throws Exception {
        List<AddAuctionDto> auctions = new ArrayList<>();
        auctions.add(addAuctionDto);
        client.start();

        Request req = client.POST("http://localhost:8080/auctions");

        req.header(HttpHeader.CONTENT_TYPE, "application/json");
        req.content(new StringContentProvider(gsonBuilder.create().toJson(auctions)));

        ContentResponse ct = req.send();
        logger.log(ct.getContentAsString());
        ResponseEntity responseEntity = gsonBuilder.create().fromJson(ct.getContentAsString(),ResponseEntity.class);

        client.stop();

        return responseEntity;

    }

    public Image getImage(int auctionId) throws Exception {

        client.start();

        ContentResponse res = client.GET("http://localhost:8080/auctions/img?auctionid="+auctionId);
        // Should return token? or User?
        Type responseEntityType = new TypeToken<ResponseEntity<String>>() {}.getType();
        ResponseEntity<String> responseEntity =
                gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();

        String img = responseEntity.getData();

        if(img != null ) {
            Image image;

            byte[] decodedBytes = Base64.getDecoder().decode(img);
            ByteArrayInputStream bis =new ByteArrayInputStream(decodedBytes);
            image = new Image(bis);
            if(image.getException() != null) {
                return null;
            } else {
                return image;
            }
        } else {
            return null;
        }
    }

    public Image stringToImage(String img) {
        if(img != null ) {
            Image image;

            byte[] decodedBytes = Base64.getDecoder().decode(img);
            ByteArrayInputStream bis =new ByteArrayInputStream(decodedBytes);
            image = new Image(bis);
            if(image.getException() != null) {
                return null;
            } else {
                return image;
            }
        } else {
            return null;
        }
    }
}
