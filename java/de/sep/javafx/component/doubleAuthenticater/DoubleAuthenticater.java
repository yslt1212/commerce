package de.sep.javafx.component.doubleAuthenticater;

import de.sep.domain.model.User;
import de.sep.javafx.component.doubleAuthenticater.component.EmailComponent;
import de.sep.javafx.component.doubleAuthenticater.component.TelComponent;
import de.sep.javafx.services.AuthenticationService;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

public class DoubleAuthenticater extends AnchorPane {

    SimpleIntegerProperty success;
    @FXML
    Button submitButton;
    @FXML
    RadioButton emailChoice;
    @FXML
    RadioButton telChoice;
    @FXML
    AnchorPane content;

    EmailComponent emailComponent;
    TelComponent telComponent;

    String email = "";

    String tel = "";

    int sendType = 0;
    AuthenticationService authenticationService;

    Random random = new Random();
    public void setUp(SimpleIntegerProperty success, AuthenticationService authenticationService) {
        this.success = success;
        try {
            this.authenticationService = authenticationService;
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/doubleAuth.fxml"));
            fxml.setController(this);
            fxml.setRoot(this);
            fxml.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int telCode = random.nextInt(80000) + 10000;
        tel = Integer.toString(telCode);

        emailComponent = new EmailComponent(authenticationService, success, email, this::deactivateRadioButtons);
        telComponent = new TelComponent(authenticationService, success, tel, this::deactivateRadioButtons);
        setContent(emailComponent);
    }

    private void setContent(AnchorPane pane) {
        content.getChildren().clear();
        content.getChildren().add(pane);
    }

    public void changeToEmail() {
        setContent(emailComponent);
    }

    public void changeToTel() {
        setContent(telComponent);
    }


    private void deactivateRadioButtons() {
        emailChoice.setManaged(false);
        emailChoice.setVisible(false);
        telChoice.setManaged(false);
        telChoice.setVisible(false);
    }


    public DoubleAuthenticater(SimpleIntegerProperty success, AuthenticationService authenticationService, String email) {
        this.email = email;
        setUp(success, authenticationService);

    }
}
