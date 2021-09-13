package de.sep.javafx.component.offercard;

import de.sep.domain.model.Auction;
import de.sep.domain.model.User;
import de.sep.domain.dto.GetHighestBidderDto;
import de.sep.domain.dto.GetSellerDto;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;

import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;


public class AuctionCard extends Pane {
    @FXML Label auctionName;

    @FXML Label bidAmount;

    @FXML Label seller;

    @FXML Label endDate;

    @FXML TextArea description;

    @FXML Label info;

    @FXML Label rating;

    @FXML Button noteButton;

    @FXML Button bidButton;

    @FXML Rectangle auctionImg;

    @FXML Label distance;


    Router router;
    Auction auction;
    UserService userService;
    User user;
    Logger logger = new Logger(getClass().getCanonicalName());
    AuctionService auctionService;

    public AuctionCard(Router router, Auction auction, UserService userService, AuctionService auctionService) throws Exception {
        this.router = router;
        this.auction = auction;
        this.userService = userService;
        this.user = userService.getUser().get();
        this.auctionService = auctionService;
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/auctionCard.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);

        try {
            Double showPrice = auction.getBidAmount();
            if(auction.getBidAmount() <= 0.0) {
                showPrice = auction.getStartPrice();
                auction.setBidAmount(auction.getStartPrice());
            }
            fxml.load();
          if (this.user.getUserId() != this.auction.getSeller()){
              ResponseEntity<Double> response =userService.getdistance(this.user.getUserId(), this.auction.getSeller());
              distance.setText(Double.toString(response.getData())+" km");
          } else distance.setText("0 km");

            info.setVisible(false);
            if (this.user.getUserId() == auction.getSeller()){
                info.setVisible(true);
                info.setText("Ihre eigene Auktion");
                bidButton.setVisible(false);
                noteButton.setVisible(false);
            }
            if (this.user.getType() == 1) {
                bidButton.setVisible(false);
                noteButton.setVisible(false);
            }
            Timestamp timestamp = new Timestamp((System.currentTimeMillis()));
            if (this.auction.getEndDate().before(timestamp)){
                if (this.user.getUserId() == auction.getSeller()) info.setText("Eigene Auktion / abgelaufen");
                else info.setText("abgelaufen");
                bidButton.setVisible(false);
                info.setVisible(true);
            }
            ResponseEntity<List<GetHighestBidderDto>> getHighestBidderResponse = auctionService.getHighestBidder();
            if(getHighestBidderResponse.getStatus() == 200){
                List<GetHighestBidderDto> list = getHighestBidderResponse.getData();
                if (list != null){
                    for (int i=0; i<list.size();i++){
                        if (list.get(i).getAuctionid() == this.auction.getAuctionId()){
                            if(this.user.getUserId() == list.get(i).getUserid()){
                                info.setText("Sie sind Höchstbietender");
                                info.setVisible(true);
                            }
                        }
                    }
                }
            }

            setAuctionName(auction.getOffername());
            ResponseEntity<GetSellerDto> responseEntity = userService.getSeller(auction.getSeller());
            GetSellerDto getSellerDto = responseEntity.getData();
            setSeller(getSellerDto.getUsername());
            setEndDate(auction.getEndDate().toString());
            setPrice(Double.toString(showPrice));
            setDescription(auction.getDescription());
            setAuctionImg(auctionService.getImage(auction.getAuctionId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setAuctionName(String t) {
        auctionName.setText(t);
    }

    public void onBid() {
        HashMap<String, String> params = new HashMap();
        params.put("auctionid", Integer.toString(auction.getAuctionId()));
        router.route("/bidOnAuction", params);
    }

    public void onNote() {
        ResponseEntity responseEntity = userService.noteAuction(auction.getAuctionId());
        info.setVisible(true);
        if (responseEntity.getStatus() == 200) info.setText("Auktion gemerkt");
        if (responseEntity.getStatus() == 500) info.setText("Auktion bereits gemerkt");
        logger.log(responseEntity.getStatus());
    }

    public void setPrice(String t) {
        bidAmount.setText(t);
    }

    public void setSeller(String t) {
        seller.setText(t);
    }

    public void setEndDate(String t) {
        endDate.setText(t);
    }

    public void setDescription(String t) {
        description.setText(t);
    }

    public void setRating(String t) {
        rating.setText(t);
    }

    public void setAuctionImg(Image img){

        if(img != null) {
            auctionImg.setFill(new ImagePattern(img));
        }
    }

}
