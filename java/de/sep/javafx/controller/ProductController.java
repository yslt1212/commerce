package de.sep.javafx.controller;

import de.sep.domain.dto.AddProductDto;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.CatalogFilter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class ProductController implements Routable {

    @FXML
    TextField categoryField;

    @FXML
    TextField productNameField;

    @FXML
    TextField priceField;

    @FXML
    TextArea productDescriptionField;

    @FXML
    Label info;

    @FXML
    RadioButton pickUp;

    @FXML
    RadioButton deliver;


    Router router;
    ProductService productService;


    private UserService userService;

    public void setProductService(ProductService productService) {

        this.productService = productService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void addCsv(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Bitte CSV öffnen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV - Files", "*.csv")
        );

        int deliveryType = 0;

        if(!pickUp.isSelected()) {
            deliveryType = 1;
        }

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        List<AddProductDto> pdtos = new ArrayList<>();
        int counter = 0;
        try {
            Scanner reader = new Scanner(selectedFile, StandardCharsets.ISO_8859_1);
            while (reader.hasNextLine()){
                if(counter > 0) {
                    String thisLine = reader.nextLine();
                    String[] splittedLine = thisLine.split(";");
                    AddProductDto pdto = new AddProductDto(splittedLine[0], userService.getUser().get().getUserId(), Double.parseDouble(splittedLine[2].replace(",", ".")), splittedLine[1], splittedLine[3], "", deliveryType);
                    pdtos.add(pdto);
                    counter++;
                } else {
                    reader.nextLine();
                    counter++;
                }
            }
            productService.addProducts(pdtos);
            info.setText("Produkte hinzugefügt!");
            info.setVisible(true);
        } catch (FileNotFoundException e) {
            info.setText("Die CSV ");
            info.setVisible(true);
            e.printStackTrace();
        } catch (Exception e) {
            int zeile = counter+1;
            info.setText("Dies ist keine gültige CSV Datei, Sie haben in Zeile " + zeile + " einen Fehler!");
            info.setVisible(true);
            e.printStackTrace();
        }


    }

    public void back() {
        router.back();
    }

    public void onSubmit(){
        String category = categoryField.getText();
        String productName = productNameField.getText();
        String price = priceField.getText();
        String productDescription = productDescriptionField.getText();

        int deliveryType = 0;

        if(!pickUp.isSelected()) {
            deliveryType = 1;
        }


        if(category.length()==0 || productName.length() == 0|| price.length() == 0 || productDescription.length() ==0) {
            info.setText("Bitte alle Felder ausfüllen!");




        }else if(!priceField.getText().matches("(0|[1-9]\\d*)")){
            info.setText("Bitte Zahlen einfügen! ");
        }
        else {

            try {
                AddProductDto pdto = new AddProductDto(productName, userService.getUser().get().getUserId(),Integer.parseInt(price), category,  productDescription, "", deliveryType);
                productService.addProduct(pdto);
                info.setText("Produkte hinzugefügt");
                info.setVisible(true);
            } catch (Exception e) {
                info.setText(e.getMessage());
                info.setVisible(true);
            }
        }


        info.setVisible(true);
    }

    @Override
    public void setRouter(Router router) {

    }

    @Override
    public void onRoute() {
        productNameField.clear();
        priceField.clear();
        categoryField.clear();
        productDescriptionField.clear();
        pickUp.setSelected(true);
    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }

    @Override
    public void onLeave() {
    reset();
    }

    private void reset() {
    info.setVisible(false);
    categoryField.setText("");
    priceField.setText("");
    productNameField.setText("");
    productDescriptionField.setText("");
    }
}
