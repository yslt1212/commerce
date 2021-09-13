package de.sep.javafx.controller;

import de.sep.domain.dto.LoggedUserDto;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuthenticationService;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import de.sep.javafx.util.ResponseEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class LoginController implements Initializable {

    Router router;
    AuthenticationService authenticationService;
    String wrongCredentialsText = "Falsches Passwort";
    String userNotFoundText = "Dieser Nutzer existiert nicht";
    String internalServerErrorText = "Leider konnte unser Server nicht erreicht werden, versuchen Sie es später erneut";

    @FXML TextField usernameField;

    @FXML TextField passwordField;

    @FXML Label info;

    public LoginController() {}

    // Route to Register
    public void goToRegister() {
        router.route("/register");
    }

    // Route to forgotPassword
    public void goToForgotPassword() {
        router.route("/forgotPassword");
    }


    // log the user in
    public void login() throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String token = "";
        info.setVisible(true);
        try {
            ResponseEntity<LoggedUserDto> responseEntity= authenticationService.login(username, password);
            switch (responseEntity.getStatus()) {
                case 403:
                    info.setText(wrongCredentialsText);
                    break;
                case 404:
                    info.setText(userNotFoundText);
                    break;
                case 200:
                    info.setVisible(false);
                    // placeholder should route to navigation
                    router.route("/navigation");

                    // Since the navigation itself doesnt display any content, we have to tell it to show
                    // the home
                    // parts
                    HashMap<String, String> params = new HashMap<>();
                    params.put("userid", Integer.toString(responseEntity.getData().getUserid()));
                    router.route("/home");
                    resetToStart();
                    break;

                default:
                    info.setText(internalServerErrorText);
                    break;


            }

        } catch (Exception e) {
            info.setText(internalServerErrorText);
            e.printStackTrace();
        }

    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resetToStart() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
