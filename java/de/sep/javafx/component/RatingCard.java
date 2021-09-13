package de.sep.javafx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class RatingCard extends Pane {

    @FXML
    Label ratingLabel;

    public RatingCard(){
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/component/ratingCard.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);
        try {
            fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ratingLabel.setText("? / 5");
    }

    public RatingCard(float rating){
        this();
        setRating(rating);
    }

    public void setRating(float rating){
        if(rating > 5 || rating < 0){
            throw new IllegalArgumentException("rating can't be smaller than 0 or larger than 5");
        }else{
            ratingLabel.setText(String.format("%d / 5",(int)Math.floor(rating)));
        }
    }

}
