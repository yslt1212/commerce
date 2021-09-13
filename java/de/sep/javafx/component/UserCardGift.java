package de.sep.javafx.component;



import de.sep.domain.dto.UpdateVoucherDto;
import de.sep.domain.model.UserGiftCard;
import de.sep.javafx.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;


public class UserCardGift extends HBox {

    TextField tf;
    @FXML
    Circle userCircle;
    @FXML
    Label userNameLabel;
    @FXML
    Button sendButton;
    UserGiftCard userGiftCard;
    UserService userService;
    public UserCardGift(UserGiftCard userGiftCard, UserService userService,TextField tf){
        this.userService=userService;
        this.userGiftCard=userGiftCard;
        this.tf=tf;
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/component/userCardVoucherGift.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);
        try {

            fxml.load();
            userNameLabel.setText(userGiftCard.getUsername());
            ImagePattern userPicture=new ImagePattern(convertStringToImage(userGiftCard.getImg()));
            userCircle.setFill(userPicture);
            sendButton.setOnAction(actionEvent -> {
                UpdateVoucherDto uvDto=new UpdateVoucherDto(userGiftCard.getUserid(),tf.getText());
                try {
                    userService.voucherChange(uvDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private Image convertStringToImage(String s){
    Image image;
        byte[] decodedBytes = Base64.getDecoder().decode(s);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        image = new Image(bis);


        return image;

    }
}
