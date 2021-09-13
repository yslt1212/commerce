package de.sep.javafx.controller;

import de.sep.domain.dto.GetVoucherDto;
import de.sep.domain.dto.VoucherDto;
import de.sep.domain.model.User;
import de.sep.javafx.component.ChargeWalletCard;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.interfaces.IUserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class WalletController implements Routable, Initializable {

    Router router;

    @FXML
    Label balanceLabel;

    @FXML
    VBox chargeCardContainer;

    @FXML
    TextField customChargeField;

    @FXML
    Label greeterLabel;

    @FXML
    TextField voucherCode;

    @FXML
    Button sendCode;

    @FXML
    Label infoLabel;

    @FXML
    Button value1;

    @FXML
    Button value2;

    @FXML
    Button value3;

    @FXML
    Button value4;

    @FXML
    TextField generatedCode;

    List<ChargeWalletCard> chargeWalletCards = new ArrayList<>();

    IUserService userService;

    private void addChargeWalletCards(IUserService userService){
        int[] chargeValues = {5,10,20,50};
        for(int i=0;i< chargeValues.length;i++){
            ChargeWalletCard chargeWalletCard = new ChargeWalletCard();
            chargeWalletCard.setAmount(chargeValues[i]);
            chargeWalletCard.setUserService(userService);
            chargeWalletCards.add(chargeWalletCard);
        }
        chargeCardContainer.getChildren().addAll(0,chargeWalletCards);
        chargeCardContainer.layout();
    }

    public void setUserService(IUserService userService) throws Exception {
        this.userService = userService;
        chargeWalletCards.forEach(chargeWalletCard -> chargeWalletCard.setUserService(userService));



    }

    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute() {

        infoLabel.setVisible(false);
        try {
            ObjectProperty<User> user = this.userService.getUser();
            if(user.get() != null) {
                user.addListener((observable, User, currentUser) -> {
                    if(currentUser != null) {
                        this.balanceLabel.setText(String.format("%.2f $EP",currentUser.getBalance()));
                    }

                });
            }
            balanceLabel.setText(String.format("%.2f $EP",this.userService.getUser().getValue().getBalance()));
            greeterLabel.setText(String.format("Hallo %s, wie viel Geld möchtest du aufladen?",userService.getUser().get().getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(chargeWalletCards.size()==0){
            addChargeWalletCards(this.userService);
        }
    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }


    public void onCustomCharge() {
        if(customChargeField.getText().matches("(0|[1-9]\\d*)")){
            double amount = Double.parseDouble(customChargeField.getText());
            try {
                ResponseEntity<Double> responseEntity = userService.chargeWallet(amount);

                if(responseEntity.getStatus() == 200) {
                    userService.updateUser();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            customChargeField.setText("");
        }
    }

    @Override
    public void onLeave() {

    }

    public void onSendCode() throws Exception {
     try {
         String code = voucherCode.getText();
         System.out.println("der Gutschein code ist "+code);
         ResponseEntity<GetVoucherDto> voucher=userService.getVoucher(code);
         GetVoucherDto gvdto=voucher.getData();

         if(gvdto!=null&&gvdto.getActive()==1){
             System.out.println(gvdto.getValue());
             userService.chargeWallet(gvdto.getValue());
             userService.deactivateVoucher(code);
             userService.updateUser();
             infoLabel.setText("Gutschein wurde erfolgreich eingelöst");
             infoLabel.setVisible(true);
         }
         else{
             System.out.println("der status ist "+voucher.getStatus());
             infoLabel.setText("Gutschein Code nicht gefunden");
             infoLabel.setVisible(true);





         }

     } catch (Exception e) {
         e.printStackTrace();
     }

    }


    public void generateCode(int value, int length) throws Exception{
        try{

            Random rnd = new Random();
            char[] voucher = new char[length];
            voucher[0] = (char) (rnd.nextInt(9) + '1');
            for (int i = 1; i < length; i++) {
                voucher[i] = (char) (rnd.nextInt(10) + '0');
            }

            if(userService.getUser().get().getBalance() > value) {
                infoLabel.setText("");
                infoLabel.setVisible(true);
                userService.addVoucher(userService.getUser().get().getUserId(), value, String.valueOf(voucher));
                int neu = 0;
                neu -= value;
                generatedCode.setText(String.valueOf(voucher));
                userService.chargeWallet(neu);
                userService.updateUser();
            }else{
                infoLabel.setText("Guthaben nicht ausreichend!");
                infoLabel.setVisible(true);
            }

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

            value1.setOnAction(actionEvent -> {

                try{
                    generateCode(5,12);
                }catch (Exception e){

                }
            });

        value2.setOnAction(actionEvent -> {

            try{
                generateCode(10,12);
            }catch (Exception e){

            }
        });

        value3.setOnAction(actionEvent -> {

            try{
              generateCode(25,12);
            }catch (Exception e){

            }
        });

        value4.setOnAction(actionEvent -> {

            try{
             generateCode(50,12);
            }catch (Exception e){

            }
        });

    }
}
