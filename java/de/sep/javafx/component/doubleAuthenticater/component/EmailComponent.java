package de.sep.javafx.component.doubleAuthenticater.component;

import de.sep.javafx.services.AuthenticationService;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.concurrent.Callable;

public class EmailComponent extends AnchorPane {

    AuthenticationService authenticationService;
    SimpleIntegerProperty success;
    String email;
    @FXML
    TextField inputField;
    @FXML
    Label inputLabel;
    @FXML
    Button submitButton;

    Runnable deactivateRadioButtons;


    public EmailComponent (AuthenticationService authenticationService, SimpleIntegerProperty success, String email, Runnable deactivateRadioButtons) {
        this.deactivateRadioButtons = deactivateRadioButtons;
        this.success = success;
        this.email = email;
        try {
            this.authenticationService = authenticationService;
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/component/doubleAuthEmail.fxml"));
            fxml.setController(this);
            fxml.setRoot(this);
            fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        submitButton.setOnAction(actionEvent -> {
            handleCodeSend(getCode());
        });
        inputField.setText(email);
    }

    private ResponseEntity getCode() {
        return authenticationService.getDoubleAuthCode(email, 0);
    }


    private void changeToCode() {
        this.deactivateRadioButtons.run();
        inputLabel.setText("Code:");
        inputField.setText("");
        inputField.setEditable(true);
    }

    private void handleCodeSend(ResponseEntity responseEntity) {
        if(responseEntity.getStatus() == 200) {
            changeToCode();
            submitButton.setOnAction((actionEvent -> {
                try {
                    ResponseEntity<Boolean> codeResponse = authenticationService.checkDoubleAuthCode(email, 0, Integer.parseInt(inputField.getText()));
                    if(codeResponse.getStatus() == 200) {
                        handleCodeResponse(codeResponse);
                    }
                } catch (NumberFormatException e) {
                    success.set(0);
                }

            }));
        }
    }
    private void handleCodeResponse(ResponseEntity<Boolean> responseEntity) {
        if(responseEntity.getData()) {
            success.set(1);
        }  else {
            success.set(0);
        }
    }
}
