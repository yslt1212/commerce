package de.sep.javafx.controller;

import de.sep.domain.model.User;
import de.sep.domain.dto.UpdateUserDto;
import de.sep.javafx.component.doubleAuthenticater.DoubleAuthenticaterDialog;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuthenticationService;
import de.sep.javafx.services.interfaces.IUserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class EditProfileController implements Routable {

    private Router router;

    IUserService userService;
    EmailValidator emailValidator = EmailValidator.getInstance();

    @FXML
    Circle avatar;

    @FXML
    Label nameLabel;

    @FXML
    TextField nameField;

    @FXML
    TextField emailField;

    @FXML
    TextField streetField;

    @FXML
    TextField cityField;

    @FXML
    TextField postcodeField;

    @FXML
    PasswordField oldPwField;

    @FXML
    PasswordField newPwField;

    @FXML
    PasswordField newPwFieldRepeat;

    @FXML
    Label info;

    @FXML
    RadioButton twoFactorAuthToggler;

    String image="";

    ObjectProperty<User> userObjectProperty;

    Boolean doubleAuthRequired = false;
    Boolean doubleAuthSuccess = false;
    DoubleAuthenticaterDialog doubleAuthenticaterDialog;
    AuthenticationService authenticationService;

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setUserService(IUserService userService){
        this.userService = userService;
    }

    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute() {

        try {

            info.setVisible(false);
            info.setText("");

            userObjectProperty = this.userService.getUser();

            userObjectProperty.addListener((observable, oldval, newval) -> {
                if(newval != null) {
                    updateUserData(newval);
                }
            });
            User user = this.userService.getUser().get();



            updateUserData(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRouteWithParams(Map<String, String> params) { }

    public void onApply() throws Exception {
        String username = nameField.getText();
        String email = emailField.getText();
        String street = streetField.getText();
        String postcode = postcodeField.getText();
        String city = cityField.getText();
        Boolean stillWantsDoubleAuth = true;
        if(userObjectProperty.get().getType() == 0) {
            stillWantsDoubleAuth = twoFactorAuthToggler.isSelected();
        }

        if (username.length() == 0
                || email.length() == 0
                || street.length() == 0
                || postcode.length() == 0
                || city.length() == 0) {
            info.setText("Bitte füllen Sie alle Felder aus");

        } else if (!emailValidator.isValid(email)) {
            info.setText("Die angegebene Email ist nicht valid");
        } else {


            if(doubleAuthRequired) {

                doubleAuthenticaterDialog = new DoubleAuthenticaterDialog(authenticationService, userObjectProperty.get().getEmail());
                doubleAuthSuccess = doubleAuthenticaterDialog.showAndWait().get();
            }

            info.setText("Anfrage wird bearbeitet");

            if(!doubleAuthRequired || (doubleAuthRequired && doubleAuthSuccess)) {

                int numericstillWantsDoubleAuth = 0;
                if(stillWantsDoubleAuth) {
                    numericstillWantsDoubleAuth = 1;
                } else {
                    numericstillWantsDoubleAuth = 0;
                }
                System.out.println(numericstillWantsDoubleAuth);

                ResponseEntity<UpdateUserDto> responseEntity = userService.updateUserData(
                        this.nameField.getText(),
                        this.emailField.getText(),
                        this.streetField.getText(),
                        this.postcodeField.getText(),
                        this.cityField.getText(),
                        this.image,
                        numericstillWantsDoubleAuth);
                if (responseEntity.getStatus() == 200) {
                    userService.updateUser();
                    info.setText("Anfrage erfolgreich bearbeitet");
                } else {
                    info.setText(responseEntity.getMessage());
                }
            }else {
                info.setText("Two - Factor - Auth fehlgeschlagen");
            }
        }
        info.setVisible(true);
    }

    public void onChangePassword() {
        String oldPassword = oldPwField.getText();
        String newPassword = newPwField.getText();
        String newPasswordRepeat = newPwFieldRepeat.getText();

        ResponseEntity responseEntity = new ResponseEntity();



        if(!newPassword.equals(newPasswordRepeat)) {
            info.setText("Passwörter stimmen nicht überein");
        } else if(oldPassword.length() == 0 || newPassword.length() == 0 || newPasswordRepeat.length() == 0) {
            info.setText("Bitte alle Felder ausfüllen");
        } else {
            if(doubleAuthRequired) {

                doubleAuthenticaterDialog = new DoubleAuthenticaterDialog(authenticationService, userObjectProperty.get().getEmail());
                doubleAuthSuccess = doubleAuthenticaterDialog.showAndWait().get();
            }
            if(!doubleAuthRequired || doubleAuthRequired && doubleAuthSuccess) {
                try {
                    info.setText("Anfrage wird bearbeitet");
                    responseEntity = userService.changePassword(oldPassword, newPassword);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (responseEntity.getStatus() == 200) {
                    info.setText("Anfrage erfolgreich bearbeitet");
                } else {
                    info.setText(responseEntity.getMessage());
                }
            } else {
                info.setText("Two - Factor - Auth fehlgeschlagen");
            }
        }

        info.setVisible(true);


    }

    public void onCancel(){
        router.route("/home");
    }

    public void onImgChange() throws IOException {

            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Bitte Foto auswählen");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image - Files", "*.jpg", "*.png"));

            File selectedFile = fileChooser.showOpenDialog(new Stage());
            byte[] fileContent = FileUtils.readFileToByteArray(selectedFile);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            image = encodedString;

        }

    private void updateUserData(User user) {
        if(user.getType() == 1) {
            this.doubleAuthRequired = true;
            twoFactorAuthToggler.setVisible(false);
        } else {
            if(user.getWantsTFA() == 1) {
                this.doubleAuthRequired = true;
                twoFactorAuthToggler.setSelected(true);
            } else {
                this.doubleAuthRequired = false;
                twoFactorAuthToggler.setSelected(false);
            }
            twoFactorAuthToggler.setVisible(true);

        }
        avatar.setFill(new ImagePattern(userService.getImage()));
        nameLabel.setText(user.getUsername());
        nameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        oldPwField.setText("");
        newPwField.setText("");
        newPwFieldRepeat.setText("");
        image=user.getImg();
        streetField.setText(user.getStreet());
        cityField.setText(user.getCity());
        postcodeField.setText(user.getPostcode());
    }

    @Override
    public void onLeave() {

    }

    public void onRating() {
        HashMap<String, String> params = new HashMap<>();

        params.put("userId", Integer.toString(userObjectProperty.get().getUserId()));
        router.route("/showProfile", params);
    }

}

