package de.sep.javafx.component;

import de.sep.domain.model.Rating;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class UserRatingCard extends AnchorPane {

    @FXML
    Label ratingUsernameLabel;
    @FXML
    Label ratingDateLabel;
    @FXML
    TextArea descriptionField;


    public UserRatingCard(Rating rating){
        System.out.println("MEOW");
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/component/userRatingCard.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);
        try {
            fxml.load();
            ratingUsernameLabel.setText(rating.getAuthorName());
            ratingDateLabel.setText("");
            descriptionField.setText(rating.getComment());
            ((VBox)ratingDateLabel.getParent()).getChildren().add(new RatingCard(rating.getRating()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
