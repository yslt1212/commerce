package de.sep.javafx.controller;

import de.sep.javafx.component.doubleAuthenticater.DoubleAuthenticaterDialog;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuthenticationService;
import java.net.URL;
import java.util.ResourceBundle;

import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.apache.commons.validator.routines.EmailValidator;

public class RegisterController implements Initializable {

    @FXML TextField usernameField;

    @FXML TextField passwordField;

    @FXML TextField passwordRepeatField;

    @FXML TextField emailField;

    @FXML TextField streetField;

    @FXML TextField postcodeField;

    @FXML TextField cityField;

    @FXML TextField companyField;

    @FXML Label info;

    @FXML Label companyLabel;

    @FXML RadioButton commercialCustomer;

    @FXML RadioButton customer;

    @FXML RadioButton twoFactorAuthToggler;


    Logger logger = new Logger(getClass().getCanonicalName());

    Boolean doubleAuthRequired = false;
    Boolean doubleAuthSuccess = false;

    Router router;
    AuthenticationService authenticationService;
    EmailValidator emailValidator = EmailValidator.getInstance();
    DoubleAuthenticaterDialog doubleAuthenticaterDialog;

    public RegisterController() throws Exception {}

    public void register() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String passwordRepeat = passwordRepeatField.getText();
        String street = streetField.getText();
        String postcode = postcodeField.getText();
        String city = cityField.getText();
        String company = companyField.getText();
        int userType = 0;


        if(commercialCustomer.isSelected()) {
            userType = 1;
        }
        Boolean success = true;

        if (username.length() == 0
                || email.length() == 0
                || password.length() == 0
                || passwordRepeat.length() == 0
                || street.length() == 0
                || postcode.length() == 0
                || city.length() == 0
                || userType == 1 && company.length() == 0) {
            info.setText("Bitte füllen Sie alle Felder aus");
            success = false;
        } else if (!password.equals(passwordRepeat)) {
            info.setText("Die Passwörter stimmen nicht überein");
            success = false;
        }else if (!emailValidator.isValid(email)) {
            info.setText("Die angegebene Email ist nicht valid");
            success = false;
        }else {
            // Emptying the companyField cause we dont need it since we r userType 0
            if(userType == 0) {
                company = "";
                companyField.setText("");
                doubleAuthRequired = twoFactorAuthToggler.isSelected();
            } else {
                doubleAuthRequired = true;
            }



            if(doubleAuthRequired) {

                doubleAuthenticaterDialog = new DoubleAuthenticaterDialog(authenticationService, email);
                doubleAuthSuccess = doubleAuthenticaterDialog.showAndWait().get();
            }

            info.setText("loading");
            info.setVisible(true);

            if(!doubleAuthRequired || doubleAuthRequired && doubleAuthSuccess) {
                try {
                    ResponseEntity<Integer> responseEntity =  authenticationService.register(
                            username, password, email, street, postcode, city,  0, "", company, userType);

                    logger.log(responseEntity.getStatus()+", ",responseEntity.getMessage());

                    if(responseEntity.getStatus() != 200) {
                        info.setText(responseEntity.getMessage());
                        success = false;
                    }

                } catch (Exception e) {
                    success = false;
                    info.setText("Irgendetwas ist schief gelaufen, versuchen Sie es später erneut");
                    e.printStackTrace();
                }
            } else {
                info.setText("Two - Factor - Auth fehlgeschlagen");
                success = false;
            }

        }
        if (success) {
            router.route("/login");
            resetToStart();
        } else {
            info.setVisible(true);
        }
    }

    public void goToLogin() {
        router.route("/login");
    }

    public Router getRouter() {
        return router;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;

    }

    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void hideCompanyNameField(ActionEvent actionEvent) {
        companyField.setVisible(false);
        companyField.setFocusTraversable(false);
        companyLabel.setVisible(false);
        twoFactorAuthToggler.setVisible(true);
        twoFactorAuthToggler.setFocusTraversable(true);
        twoFactorAuthToggler.setManaged(true);
    }

    public void showCompanyNameField(ActionEvent actionEvent) {
        companyField.setVisible(true);
        companyField.setFocusTraversable(true);
        companyLabel.setVisible(true);
        twoFactorAuthToggler.setVisible(false);
        twoFactorAuthToggler.setFocusTraversable(false);
        twoFactorAuthToggler.setManaged(false);
    }

    private void resetToStart() {
        info.setText("");
        info.setVisible(false);
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        streetField.setText("");
        postcodeField.setText("");
        passwordField.setText("");
        passwordRepeatField.setText("");
        companyField.setText("");
        cityField.setText("");

        twoFactorAuthToggler.setVisible(true);
        twoFactorAuthToggler.setFocusTraversable(true);
        twoFactorAuthToggler.setManaged(true);

        customer.setSelected(true);
        commercialCustomer.setSelected(false);

        companyField.setVisible(false);
        companyLabel.setVisible(false);

    }

    @FXML
    private void toggleTwoFactorAuth() {
        this.doubleAuthRequired = !this.doubleAuthRequired;
    }
}
