package de.sep.javafx.controller;

import de.sep.javafx.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ForgotPasswordController {

    Router router;

    @FXML TextField usernameField;

    public ForgotPasswordController() {}

    public void goToLogin() {
        router.route("/login");
    }

    public void resetPassword() {
        String username = usernameField.getText();
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }
}
