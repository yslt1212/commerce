package de.sep.javafx.component;

import de.sep.javafx.services.interfaces.IUserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ChargeWalletCard extends HBox {

    @FXML
    Label chargeWalletLabel;
    @FXML
    Label chargeWalletButtonLabel;
    @FXML
    Button chargeWalletButton;

    IUserService userService;

    int chargeAmount;

    public ChargeWalletCard(){
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/chargeWalletCard.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);
        try {
            fxml.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserService(IUserService userService){
        this.userService = userService;
    }

    public void setAmount(int amount){
        this.chargeAmount = amount;
        chargeWalletLabel.setText(String.format("%d $EP hinzufügen",amount));
        chargeWalletButtonLabel.setText(String.format("%d $EP",amount));
    }

    public void onChargePressed(){
        try {
           ResponseEntity<Double> responseEntity = userService.chargeWallet(this.chargeAmount);

           if(responseEntity.getStatus() == 200) {
               userService.updateUser();
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
