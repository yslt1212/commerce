package de.sep.javafx.controller;

import de.sep.domain.model.Order;
import de.sep.javafx.component.OrderCard;

import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.OrderFilter;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class ShowOrderController  implements Routable, Initializable{


    @FXML
    HBox orderCardContainer;

    @FXML
    ScrollPane orderScrollPane;

    @FXML
    ChoiceBox<OrderFilter.SortByDate> showOrder_Date;

    SimpleObjectProperty<OrderFilter> filter;
    SimpleListProperty<Order> allOrders;


    ProductService productService;
    UserService userService;
    Router router;
    Logger logger = new Logger(getClass().getCanonicalName());
    public ShowOrderController(){
        filter = new SimpleObjectProperty<>(new OrderFilter());
        allOrders = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<Order>()));

    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute() {

        try {
            ResponseEntity<List<Order>> listorders = productService.getMyOrders();
            if (listorders.getStatus()== 200) allOrders.setAll(listorders.getData());
            else allOrders.setAll(new ArrayList<>());


        }catch (Exception e){
            e.printStackTrace();

        }

    }




    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }



    @Override
    public void onLeave() {

    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
// showOrder_Date.setConverter(new StringConverter<>() {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showOrder_Date.setConverter(new StringConverter<>() {
            @Override
            public String toString(OrderFilter.SortByDate sortByDate) {
                switch(sortByDate) {
                    case BY_ONGOING -> {
                        return "Aktuelle Bestellungen";
                    }
                    case BY_PAST -> {
                        return  "Vergangene Bestellungen";
                    }
                    default ->{
                        return  "undefinded";
                    }
                }

            }

            @Override
            public OrderFilter.SortByDate fromString(String s) {
                return null;
            }
        });


        //Fill Choicebox
        showOrder_Date.getItems().addAll(OrderFilter.SortByDate.values());
        showOrder_Date.getSelectionModel().selectFirst();

        //Filter Listener
        showOrder_Date.getSelectionModel().selectedItemProperty().addListener((observable, oldSorting, newSorting) -> {
                    OrderFilter orderFilter = filter.get();
                   orderFilter = orderFilter.withSortByDate(newSorting);
                    filter.set(orderFilter);
        });

        filter.addListener((observable, oldFilter, newFilter) -> {

                        updateOrderCards(filterOrders(newFilter, allOrders.get()));

                });
        //Orders Listener
       allOrders.addListener((observable, oldOrders, newOrders) -> {
          logger.log(newOrders);
           updateOrderCards(filterOrders(filter.get(), newOrders));

       });



    }

    private List<Order> filterOrders(OrderFilter filter, List<Order> orders) {
        List<Order> filteredOrders =  new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        switch (filter.getSortByDate()){
            case BY_PAST -> {
                // Filter
                filteredOrders.addAll(orders);

                filteredOrders = filteredOrders.stream().filter(order -> order.getDeliveryDate().before(now)).collect(Collectors.toList());

            }
            // Filter
            case BY_ONGOING -> {
                filteredOrders.addAll(orders);
                filteredOrders = filteredOrders.stream().filter(order -> order.getDeliveryDate().after(now)).collect(Collectors.toList());
            }

        }
        return filteredOrders;
    }


    private void updateOrderCards(List<Order> orders){
        orderCardContainer.getChildren().clear();
        orders.forEach(order -> {
            try {
                OrderCard orderCard = new OrderCard(router,order,userService, productService);
                orderCardContainer.getChildren().add(orderCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }




    }













