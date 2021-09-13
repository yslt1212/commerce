package de.sep.javafx.controller;


import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapComponentInitializedListener;
import com.dlsc.gmapsfx.javascript.object.*;
import com.dlsc.gmapsfx.javascript.object.Marker;
import com.google.maps.model.LatLng;
import de.sep.domain.dto.GetSellerDto;
import de.sep.domain.model.Auction;
import de.sep.domain.model.Offer;
import de.sep.domain.model.User;
import de.sep.javafx.component.offercard.OfferCard;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

public class BidOnAuctionController implements Routable, Initializable, MapComponentInitializedListener{
    AuctionService auctionService;
    UserService userService;
    Router router;
    Auction auction;
    Image auctionImg;
    @FXML
    Rectangle img;

    @FXML
    Label nameLabel;

    @FXML
    Label priceLabel;

    @FXML
    Label endDateLabel;

    @FXML
    Label descriptionLabel;

    @FXML
    TextField bidField;

    @FXML
    Label deliveryTypeLabel;

    @FXML
    Label info;
    @FXML
    Label bewertungLabel;
    @FXML
    Label sellerLabel;
    @FXML
    TextField perimeterField;
    @FXML
    protected GoogleMapView mapView;
    @FXML
    ScrollPane offerScrollPane;
    @FXML
    HBox offerCardContainer;
    @FXML
    Label infoMap;

    String deliveryPickUp = "Nein";

    String deliveryDelivery = "Ja";

    Logger logger = new Logger(getClass().getCanonicalName());

    GoogleMap gmap;

    double perimeter;

    User user;


    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute() {


    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

        try {
            user = userService.getUser().get();
            perimeter = 5;
            this.filterAuctions();

            int auctionid = Integer.parseInt(params.get("auctionid"));
            ResponseEntity<Auction> responseEntity = auctionService.getAuctionById(auctionid);

            if(responseEntity.getStatus() == 200) {
                 auction = responseEntity.getData();
            }

            auctionImg = auctionService.getImage(auctionid);

            if(auctionImg != null) {
                setImg(auctionImg);
            }
            setNameLabel(auction.getOffername());
            if(auction.getBidAmount()<= 0.0) {
                setPriceLabel(auction.getStartPrice());
            } else {
                setPriceLabel(auction.getBidAmount());
            }

            setEndDateLabel(auction.getEndDate().toString());
            setDescriptionLabel(auction.getDescription());


            GetSellerDto getSellerDto = userService.getSeller(auction.getSeller()).getData();
            try {
                bewertungLabel.setText(Float.toString(userService.getAverageRating(auction.getSeller()).getData()));
            } catch (Exception e) {
                bewertungLabel.setText("Bisher nicht bewertet");
            }

            sellerLabel.setText(getSellerDto.getUsername());
            switch (auction.getDeliveryType()) {
                case 0 -> {
                    setDeliveryTypeLabel(deliveryPickUp);
                }
                case 1 -> {
                    setDeliveryTypeLabel(deliveryDelivery);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLeave() {
        reset();
    }

    private void reset() {
        bidField.setText("");
        info.setText("");
        perimeter = 5;

    }


    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setImg(Image img) {
        try {
            this.img.setFill(new ImagePattern(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNameLabel(String t) {
        this.nameLabel.setText(t);
    }

    public void setPriceLabel(Double t) {
        this.priceLabel.setText(Double.toString(t));
    }

    public void setEndDateLabel(String t) {
        this.endDateLabel.setText(t);
    }

    public void setDescriptionLabel(String t) {
        this.descriptionLabel.setText(t);
    }

    public void setDeliveryTypeLabel(String t) {
        this.deliveryTypeLabel.setText(t);
    }

    public void setInfo(String t) {

        this.info.setText(t);
        info.setVisible(true);
    }

    public void onBid() {
        try {
            double bid = Double.parseDouble(bidField.getText());
            if(bid <= auction.getBidAmount() || bid <= auction.getStartPrice()) {
                this.setInfo("Sie müssen mehr bieten als das jetzige Gebot");
            } else {
                ResponseEntity responseEntity = userService.bidOnAuction(auction.getAuctionId(), bid);

                if(responseEntity.getStatus() == 200) {
                    setInfo(responseEntity.getMessage());

                    auction.setBidAmount(auctionService.getAuctionById(this.auction.getAuctionId()).getData().getBidAmount());
                    setPriceLabel(auction.getBidAmount());

                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.setInfo("Sie müssen eine Flißkommazahl eingeben");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onNode() {
        ResponseEntity responseEntity = userService.noteAuction(auction.getAuctionId());

        logger.log(responseEntity.getStatus());
    }

    public void back() {
        router.back();
    }


    public void perimeterFieldPressed(ActionEvent actionEvent) {
        perimeter = Double.parseDouble(perimeterField.getText());
        perimeterField.setText("");
        try {
            this.filterAuctions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapInitialized() {
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(0, 0))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(11);
        gmap = mapView.createMap(mapOptions);

        //  MarkerOptions markerOptions = new MarkerOptions();

        // markerOptions.position( new LatLong(47.6, -122.3) )
        //       .visible(Boolean.TRUE)
        //     .title("My Marker");

//        Marker marker = new Marker(markerOptions);

//        gmap.addMarker(marker);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapView.addMapInitializedListener(this);
        mapView.setKey("AIzaSyA77VHN-8ZsuzeDHv4nXflWepqFtxRgUTE");
    }

    private void updateOfferCards(List<Offer> auctions) {
        offerCardContainer.getChildren().clear();
        auctions.forEach(offer -> {
            try {
                OfferCard offerCard = new OfferCard(offer, router, userService, auctionService);
                offerCardContainer.getChildren().add(offerCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void filterAuctions() throws Exception {
        gmap.clearMarkers();
        LatLng coordinatesUser = auctionService.getCoordinates(user.getUserId()).getData();
        gmap.setCenter(new LatLong(coordinatesUser.lat,coordinatesUser.lng));
        MarkerOptions markerOptionsUser = new MarkerOptions();
        markerOptionsUser.position( new LatLong(coordinatesUser.lat, coordinatesUser.lng) )
                .visible(Boolean.TRUE)
                .title("Ihre Adresse");
        Marker markerUser = new Marker(markerOptionsUser);
        gmap.addMarker(markerUser);
        List<Offer> filteredAuctions = new ArrayList<>();
        ResponseEntity<List<Auction>>response= auctionService.getAllAuctions();
        if (response == null){
            System.out.println("keine Daten");
            infoMap.setText("Es befinden sich keine Auktionen in Ihrer Nähe");
            offerScrollPane.setVisible(false);
            return;
        }
        List<Auction> unfilteredAuctions = response.getData();

        Map <Integer,Double> distanceMap = new HashMap<>();
        for(int i=0; i<unfilteredAuctions.size();i++){
            double distance = userService.getdistance(user.getUserId(), unfilteredAuctions.get(i).getSeller()).getData();
            distanceMap.put(unfilteredAuctions.get(i).getSeller(), distance);
        }
        for (int i=0; i<unfilteredAuctions.size();i++){
            logger.log(distanceMap.get(unfilteredAuctions.get(i).getSeller()), perimeter);
            if (unfilteredAuctions.get(i).getSeller() != user.getUserId() && distanceMap.get(unfilteredAuctions.get(i).getSeller()) <= perimeter){
                filteredAuctions.add(unfilteredAuctions.get(i));
            }
        }
        if (filteredAuctions.size() == 0) {
            infoMap.setText("Es befinden sich keine Auktionen im Umkreis von "+ perimeter +" km");
            offerScrollPane.setVisible(false);
            return;
        }

        this.updateOfferCards(filteredAuctions);
        if (filteredAuctions.size() == 1) infoMap.setText("Es befindet sich 1 Auktion im Umkreis von "+ perimeter +" km");
        else infoMap.setText("Es befinden sich "+filteredAuctions.size()+" Auktionen im Umkreis von "+ perimeter +" km");
        offerScrollPane.setVisible(true);
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng coordinates = new LatLng();
        for (int i=0; i<filteredAuctions.size();i++){
            coordinates = auctionService.getCoordinates(filteredAuctions.get(i).getSeller()).getData();
            markerOptions.position( new LatLong(coordinates.lat, coordinates.lng) )
                    .visible(Boolean.TRUE)
                    .title(userService.getSeller(filteredAuctions.get(i).getSeller()).getData().getUsername());
            com.dlsc.gmapsfx.javascript.object.Marker marker = new Marker(markerOptions);
            gmap.addMarker(marker);
        }
    }
}
