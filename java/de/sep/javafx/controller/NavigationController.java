package de.sep.javafx.controller;

import de.sep.domain.model.User;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;

import java.awt.image.BufferedImage;
import java.util.Map;

import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class NavigationController implements Routable{

    @FXML
    Button addProductBtn;

    @FXML
    Button walletButton;

    @FXML
    Button messageButton;

    @FXML
    Button ViewProduct;

    @FXML
    Button Rating;

    @FXML
    BorderPane navigationBorderPane;
    @FXML
    Button sendVoucherButton ;

    Router router;
    private UserService userService;

    String goToAddProductBtnText = "Produkte Hinzufügen";

    String goToAddAuctionBtnText = "Auktion hinzufügen";

    Logger logger = new Logger(getClass().getCanonicalName());

    public void setRouter(Router router) {
        this.router = router;
    }

    public NavigationController() {
    }

    public void initialize(){
        messageButton.setOnAction(event -> router.route("/conversations"));
    }

    public void goToHome() {
        router.route("/home");
    }


    // wieder zu catalog
    public void goToCatalog(ActionEvent actionEvent) {
        router.route("/catalog");
    }

    public void onBalancePressed(ActionEvent actionEvent) {
        router.route("/user/wallet");
    }

    public void onSendVoucher(ActionEvent actionEvent) {
        router.route("/sendVoucher");
    }

    public void logout(ActionEvent actionEvent) {
        userService.logout();
        router.clearRoutingHistory();
        router.route("/login");
    }
    public void goToAddProduct(ActionEvent actionEvent){
        router.route("/product");
    }

    public void goToAddAuction(ActionEvent actionEvent) {router.route("/addAuction");}

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onRoute() {
        try {
            this.userService.getUser().addListener((observable, User, user) -> {
                if(user != null) {
                    this.walletButton.setText(String.format("%.2f $EP",user.getBalance()));
                }
            });

            handleUserCases();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setSideBar(Pane pane) {
        this.navigationBorderPane.setLeft(pane);
    }

    public void clearSideBar() {
        this.navigationBorderPane.setLeft(null);
    }

    public void onProfilePressed() {
        router.route("/user/edit");
       }
    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }

    private void handleUserCases() throws Exception{

        User user = userService.getUser().get();

        switch (user.getType()) {
            case 1 -> {
                addProductBtn.setOnAction(actionEvent -> {
                    goToAddProduct(actionEvent);
                });
                addProductBtn.setText(goToAddProductBtnText);

                // Hiding wallet Button
                walletButton.setDisable(true);
                walletButton.setVisible(false);
                walletButton.setManaged(false);
                sendVoucherButton.setVisible(false);
                sendVoucherButton.setDisable(true);
                sendVoucherButton.setManaged(false);
            }
            case 0 -> {
                addProductBtn.setOnAction(actionEvent -> {
                    goToAddAuction(actionEvent);
                });
                addProductBtn.setText(goToAddAuctionBtnText);

                this.walletButton.setText(String.format("%.2f $EP",user.getBalance()));

                // Showing wallet Button
                walletButton.setDisable(false);
                walletButton.setVisible(true);
                walletButton.setManaged(true);
                sendVoucherButton.setVisible(true);
                sendVoucherButton.setDisable(false);
                sendVoucherButton.setManaged(true);

            }
        }
    }

    @Override
    public void onLeave() {

    }

}
