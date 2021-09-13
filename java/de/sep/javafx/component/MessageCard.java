package de.sep.javafx.component;

import de.sep.domain.model.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageCard extends AnchorPane {

    static DateFormat timeFormat = new SimpleDateFormat("HH:MM");

    @FXML
    Label authorLabel;

    @FXML
    Label messageLabel;

    @FXML
    Label timeLabel;

    public MessageCard(String message,String author,long timestamp){
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/component/messageCard.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);
        try{
            fxml.load();
            updateContent(message,author,timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateContent(String message,String author,long timestamp){
        authorLabel.setText(author);
        messageLabel.setText(message);
        timeLabel.setText(timeFormat.format(new Date(timestamp)));
    }
}
