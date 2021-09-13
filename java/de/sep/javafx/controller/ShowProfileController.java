package de.sep.javafx.controller;

import de.sep.domain.model.Rating;
import de.sep.javafx.component.RatingCard;
import de.sep.javafx.component.UserRatingCard;
import de.sep.domain.dto.GetRatingDto;
import de.sep.domain.dto.GetSellerDto;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.RatingService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;


public class ShowProfileController implements Routable {

    Router router;
    @FXML
    Circle userImage;
    @FXML
    Label nameLabel;
    @FXML
    Label contactLabel;
    @FXML
    Label companyNameLabel;
    @FXML
    VBox ratingCardContainer;
    @FXML
    VBox userRatingCardContainer;

    @FXML
    Button sendMessageButton;

    UserService userService;

    RatingService ratingService;

    private int userId;

    public void initialize(){
        sendMessageButton.setOnAction(event -> router.route("/chat",Map.of("partnerId",String.valueOf(userId))));
    }

    public void setUserService(UserService userService) {
        this.userService=userService;
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public void setRouter(Router router) {
        this.router = router;
    }



    @Override
    public void onRoute() {

    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {
        userId = Integer.parseInt(params.get("userId"));

        try {
            ResponseEntity<GetSellerDto> sellerResponse = userService.getSeller(userId);
            if(sellerResponse.getStatus() == 200){
                GetSellerDto seller = sellerResponse.getData();
                nameLabel.setText(seller.getUsername());
                contactLabel.setText("Kontakt:"+seller.getEmail());
                companyNameLabel.setText(seller.getCompanyName());
                ResponseEntity<List<GetRatingDto>> ratingsResponse = ratingService.getRating(userId);
                userRatingCardContainer.getChildren().clear();
                if(ratingsResponse.getStatus() == 200) {
                    float averageRating = 0;
                    List<GetRatingDto> ratings = ratingsResponse.getData();
                    if (ratings != null){
                        for (GetRatingDto ratingDto : ratings) {
                            Rating rating = new Rating(ratingDto.getRatingId(), ratingDto.getRating(), ratingDto.getUserId(), ratingDto.getAuthorName(), ratingDto.getComment());
                            UserRatingCard userRatingCard = new UserRatingCard(rating);
                            averageRating += rating.getRating();
                            userRatingCardContainer.getChildren().add(userRatingCard);
                        }
                    averageRating /= ratings.size();
                    setRatingCard(new RatingCard(averageRating));
                    }
                }else{
                    setRatingCard(new RatingCard());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setRatingCard(RatingCard ratingCard){
        if(ratingCard != null){
            ratingCardContainer.getChildren().clear();
            ratingCardContainer.getChildren().add(ratingCard);
        }else{
            ratingCardContainer.getChildren().clear();
            ratingCardContainer.getChildren().add(new RatingCard());
        }
    }

    @Override
    public void onLeave() {

    }

    public void onBack() {
        router.back();
    }


}
