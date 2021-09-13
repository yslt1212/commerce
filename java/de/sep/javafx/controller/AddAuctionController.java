package de.sep.javafx.controller;


import de.sep.domain.dto.AddAuctionDto;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

public class AddAuctionController implements Routable {

    Router router;

    @FXML
    TextField nameField;

    @FXML
    TextField startPriceField;

    @FXML
    DatePicker endDateField;

    @FXML
    TextArea descriptionField;

    @FXML
    RadioButton pickUp;

    @FXML
    RadioButton deliver;

    @FXML
    Rectangle productImg;

    @FXML
    Label info;

    AuctionService auctionService;
    UserService userService;
    String image = "";

    Logger logger = new Logger(getClass().getCanonicalName());

    public AddAuctionController() {
    }

    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void onSubmit() {
        AddAuctionDto addAuctionDto = new AddAuctionDto();
        try {
            if(
                image.length() == 0 ||
                nameField.getText().equals("")||
                startPriceField.getText().equals("") ||
                endDateField.getValue() == null ||
                descriptionField.getText().equals("")
            ) {
                info.setText("Bitte alle Felder ausfüllen");
            } else {

                if(endDateField.getValue().toString().equals("")) {
                    info.setText("Bitte alle Felder ausfüllen");
                }else {
                    String offername = nameField.getText();
                    // Need to check if rly double here
                    String startPriceString = startPriceField.getText();
                    double startPrice = Double.parseDouble(startPriceString);
                    LocalDate date = endDateField.getValue();
                    Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
                    String description = descriptionField.getText();
                    int deliveryType = 0;

                    if(!pickUp.isSelected()) {
                        deliveryType = 1;
                    }

                    addAuctionDto.setSeller(userService.getUser().get().getUserId());
                    addAuctionDto.setOffername(offername);
                    addAuctionDto.setStartPrice(startPrice);
                    addAuctionDto.setEndDate(timestamp);
                    addAuctionDto.setDescription(description);
                    addAuctionDto.setDeliveryType(deliveryType);
                    addAuctionDto.setImg(image);


                    ResponseEntity responseEntity = auctionService.addAuction(addAuctionDto);
                    info.setText(responseEntity.getMessage());
                }

            }


        } catch (NumberFormatException e) {
            info.setText("Bitte geben Sie korrekte Werte an");
        } catch (NullPointerException e) {
            info.setText("Bitte geben Sie korrekte Werte an");
        }
        catch (Exception e) {
              e.printStackTrace();
        }
        info.setVisible(true);


    }

    public void addImg() {
        try {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Bitte Foto auswählen");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image - Files", "*.jpg", "*.png"));

            File selectedFile = fileChooser.showOpenDialog(new Stage());
            byte[] fileContent = FileUtils.readFileToByteArray(selectedFile);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            image = encodedString;

            productImg.setFill(new ImagePattern(auctionService.stringToImage(image)));

        } catch (Exception e) {

        }
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute() {

    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }

    @Override
    public void onLeave() {
        reset();
    }

    private void reset() {
        nameField.setText("");
        startPriceField.setText("");
        descriptionField.setText("");
        info.setText("");
        pickUp.setSelected(true);
        deliver.setSelected(false);
        image = "";
        productImg.setFill(null);
        endDateField.setValue(null);
    }

    public void back() {
        router.back();
    }
}
