package de.sep.javafx.controller;

import de.sep.domain.dto.GetRatingDto;
import de.sep.domain.model.Rating;
import de.sep.domain.model.UserGiftCard;
import de.sep.javafx.component.RatingCard;
import de.sep.javafx.component.UserCardGift;
import de.sep.domain.dto.GetUserGiftDto;
import de.sep.javafx.component.UserRatingCard;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;


import java.util.List;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SendVoucherController implements Routable {

    UserService userService;
    Router router;

    @FXML
    TextField voucherCodeTextField;
    @FXML
    Label InfoLabel;
    @FXML
    VBox userGiftCardContainer;

    @Override
    public void setRouter(Router router) {
        this.router=router;

    }

    @Override
    public void onRoute() {

        try{
            System.out.println(userService.getusergifts(userService.getUser().getValue().getUserId()));
        InfoLabel.setVisible(false);
        ResponseEntity<List<GetUserGiftDto>> usersresponse = userService.getusergifts(userService.getUser().getValue().getUserId());
        userGiftCardContainer.getChildren().clear();
            if(usersresponse.getStatus() == 200) {
                List<GetUserGiftDto> users = usersresponse.getData();
                if (users != null){
                    for (GetUserGiftDto userGiftDto : users) {
                        UserGiftCard card = new UserGiftCard(userGiftDto.getImg(), userGiftDto.getUsername(),userGiftDto.getUserid());
                       UserCardGift userGiftCard = new UserCardGift(card,userService,voucherCodeTextField);

                        userGiftCardContainer.getChildren().add(userGiftCard);
                    }

                }
            }else{
              InfoLabel.setText("Es wurden keine User gefunden");
            }


    }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }

    @Override
    public void onLeave() {

    }
    public void setUserService(UserService userService) {
        this.userService=userService;
    }
}
