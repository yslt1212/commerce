package de.sep.javafx.component.doubleAuthenticater.component;

import com.sun.javafx.scene.control.InputField;
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

public class TelComponent extends AnchorPane {
    private SimpleIntegerProperty success;
    private Runnable deactivateRadioButtons;
    AuthenticationService authenticationService;
    String tel;
    @FXML
    Button submitButton;
    @FXML
    TextField inputField;
    @FXML
    Label inputLabel;
    public TelComponent (AuthenticationService authenticationService, SimpleIntegerProperty success, String tel, Runnable deactivateRadioButtons) {
        this.deactivateRadioButtons = deactivateRadioButtons;
        this.success = success;
        this.tel = tel;
        try {
            this.authenticationService = authenticationService;
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/component/doubleAuthTel.fxml"));
            fxml.setController(this);
            fxml.setRoot(this);
            fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        submitButton.setOnAction(actionEvent -> {

            try {

                ResponseEntity<Boolean> codeResponse = authenticationService.checkDoubleAuthCode(tel, 1, Integer.parseInt(inputField.getText()));
                if(codeResponse.getStatus() == 200) {
                    handleCodeResponse(codeResponse);
                } else {
                    success.set(0);
                }
            } catch (NumberFormatException e) {
                success.set(0);
            }

        });
        inputLabel.setText(String.format("Bitte schreiben Sie bei Telegram unserem Bot 'sepGruppeJ_bot' /code %s \n " +
                "Dieser wird Ihnen ihren Code mitteilen, welchen Sie unten ins Feld schreiben müssen", tel));
    }

    private void handleCodeResponse(ResponseEntity<Boolean> responseEntity) {
        if(responseEntity.getData()) {
            success.set(1);
        }  else {
            success.set(0);
        }
    }
}
