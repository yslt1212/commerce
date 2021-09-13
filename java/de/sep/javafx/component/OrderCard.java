package de.sep.javafx.component;

import de.sep.domain.model.Offer;
import de.sep.domain.model.Order;
import de.sep.domain.model.User;

import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.services.UserService;

import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

public class OrderCard extends Pane {

    @FXML
    Label orderID;
    @FXML
    Label pickUp_date;
    @FXML
    Button deleteOrderBtn;
    @FXML
    Label info;
    @FXML Label price;
    @FXML Label productName;
    @FXML Label seller;
    @FXML Label pickup_label;

    @FXML
    Button rateBuyerBtn;

    Router router;
    Order order;
    ProductService productService;
    UserService userService;
    User user;
    Offer offer;
    String abhol_datum = "Abholdatum";
    String liefer_datum = "Lieferdatum";


    public OrderCard(Router router, Order order, UserService userService, ProductService productService) throws Exception {
        this.router = router;
        this.order = order;
        this.userService = userService;
        this.user = userService.getUser().get();
        this.productService = productService;

        FXMLLoader orderCardfxml = new FXMLLoader(getClass().getResource("/orderCard.fxml"));
        orderCardfxml.setRoot(this);
        orderCardfxml.setController(this);

        try {
            orderCardfxml.load();
            setOrderID(Integer.toString(order.getOrderId()));

            setPickUp_date(order.getDeliveryDate());
            setPrice(Double.toString(order.getPrice()));
            setProductName(order.getOffername());
            setSeller(order.getSeller());

            if (order.getDeliveryType() == 1){
                pickup_label.setText(liefer_datum);



            }else{
                pickup_label.setText(abhol_datum);
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            if(order.getDeliveryDate().before(now)){
                deleteOrderBtn.setVisible(false);
                deleteOrderBtn.setManaged(false);
            }else{
                deleteOrderBtn.setVisible(true);
                deleteOrderBtn.setManaged(true);
                rateBuyerBtn.setVisible(false);
                rateBuyerBtn.setFocusTraversable(false);
                rateBuyerBtn.setManaged(false);
            }


            if(this.order.getSeller().equals(user.getUsername())) {
                deleteOrderBtn.setVisible(false);
                deleteOrderBtn.setManaged(false);
                rateBuyerBtn.setVisible(true);
                rateBuyerBtn.setFocusTraversable(true);
                rateBuyerBtn.setManaged(true);
            }else;






        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void rateBuyer() {
        HashMap<String, String> params = new HashMap<>();
        params.put("buyerid", Integer.toString(order.getBuyerId()));
        router.route("/rating", params);
    }

    public void deleteOrder() throws Exception {

        ResponseEntity<Integer> responseEntity = productService.deleteOrder(order.getOrderId());

        if(responseEntity.getStatus() == 200){
            userService.updateUser();
            router.route("/showOrder");
            info.setText("Die Bestellung wurde storniert!");
        }else{
            info.setText(responseEntity.getMessage());

            info.setVisible(true);
        }



    }

    public void setOrderID(String id) {
        orderID.setText(id);
    }

    public void setPickUp_date(Timestamp date) {
        pickUp_date.setText(String.valueOf(date));
    }



    public void setSeller(String sellerName){ seller.setText(sellerName);}
    public void setPrice(String p){ price.setText(p);}
    public void setProductName(String name){productName.setText(name);}






}