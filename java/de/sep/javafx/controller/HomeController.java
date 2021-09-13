package de.sep.javafx.controller;
import de.sep.domain.mapper.OrderMapper;
import de.sep.domain.mapper.ProductMapper;
import de.sep.domain.model.*;
import de.sep.javafx.component.OrderCard;
import de.sep.javafx.component.offercard.OfferCard;
import de.sep.javafx.component.sidebar.Sidebar;
import de.sep.javafx.component.sidebar.SidebarButton;
import de.sep.domain.dto.MySellsDto;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.HomeControllerState;
import de.sep.javafx.util.HomeControllerStates;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HomeController implements Routable {

    UserService userService;
    ProductService productService;
    AuctionService auctionService;

    OrderMapper orderMapper;

    Router router;
    User user;
    @FXML
    Label welcomeLabel;

    @FXML
    AnchorPane routerParent;

    @FXML
    Circle avatar;

    @FXML
    HBox productCardContainer;

    SimpleListProperty<Object> offerList;
    private NavigationController navigationController;
    Logger logger = new Logger(getClass().getCanonicalName());
    Sidebar sidebar;
    SimpleObjectProperty<HomeControllerState> currentState;
    HomeControllerState lastViewedState;
    HomeControllerState notedState;
    HomeControllerState myAuctionsState;
    HomeControllerState myBidsState;
    HomeControllerState mySellsState;
    public HomeController(){

        orderMapper = new OrderMapper();
        offerList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        offerList.addListener(((observableValue, oldval, newval) -> {
            productCardContainer.getChildren().clear();
            if(!newval.isEmpty()) {
                newval.forEach(object -> {
                    Object oc = null;

                    if(object instanceof Offer) {
                        Offer offer = (Offer) object;
                        try {
                            oc = new OfferCard(offer, router, userService, auctionService);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(object instanceof MySellsDto) {

                        try {
                            MySellsDto mySellsDto = (MySellsDto) object;
                            Order order = orderMapper.mapMySellsDto(mySellsDto);

                            oc = new OrderCard(router,order, userService, productService);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(oc instanceof OfferCard) {
                        OfferCard offerCard = (OfferCard) oc;
                        productCardContainer.getChildren().add(offerCard);
                    } else  {
                        OrderCard orderCard = (OrderCard) oc;
                        productCardContainer.getChildren().add(orderCard);
                    }


                });
            } else {
                productCardContainer.getChildren().removeAll();
            }
        }));

        currentState = new SimpleObjectProperty<>();
        currentState.addListener((observableValue, oldval, newval) -> {
            setWelcomeLabel(newval.getLabel());
            newval.getStateFunction().run();
        });

    }

    private void handleLogin() throws Exception {
        // This gets called on login
        try {
            setUserImg();

            switch (user.getType()) {
                case 0 -> {
                    handlePrivatCustomer(user);
                }
                case 1 -> {
                    handleCommercialCustomer(user);
                }
            }

            ResponseEntity<List<Product>> responseEntity =productService.getLastViewed(user.getUserId());
            if(responseEntity.getStatus() == 200) {
                offerList.setAll(responseEntity.getData());
            }

            String one = "Willkommen zurück";
            String two = "";
            if(!responseEntity.getData().isEmpty()) {

                two = "Daran hattest du Interesse";
            } else  {

                two = "Noch keine Produkte angesehen";
            }
            lastViewedState = new HomeControllerState(one,two, HomeControllerStates.LAST_VIEWED, this::onLastViewed, userService.getUser());
            currentState.set(lastViewedState);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void handleCommercialCustomer(User user) {
        List<SidebarButton> sidebarButtons = createCommercialCustomerButtons();
        sidebar =
                new Sidebar(
                        200.0,
                        60.0,
                        true,
                        sidebarButtons,
                        "sidebarBtn",
                        user.getUsername()
                );
        navigationController.setSideBar(sidebar);
    }

    private void handlePrivatCustomer(User user) {
        // This gets called on login if user is privatCustomer
        List<SidebarButton> sidebarButtons = createPrivatCustomerSidebarButtons();
        sidebar =
                new Sidebar(
                        200.0,
                        60.0,
                        true,
                        sidebarButtons,
                        "sidebarBtn",
                        user.getUsername()
                );
        navigationController.setSideBar(sidebar);
    }


    public void handleUserChange() {
        setUserImg();
    }

    private List<SidebarButton> createPrivatCustomerSidebarButtons() {
        List<SidebarButton> btns = new ArrayList<>();



        Button lastViewedBtn = new Button();
        lastViewedBtn.setOnAction((ActionEvent event) -> {
            currentState.set(lastViewedState);
        });
        SidebarButton lastViewed = new SidebarButton("Zuletzt angesehen", null, lastViewedBtn);
        btns.add(lastViewed);

        Button notedAuctionsBtn = new Button();
        notedAuctionsBtn.setOnAction((ActionEvent event) -> {
            String s1 = "Hallo";
            String s2 = "Das sind deine gemerkten Auktionen";
            try {
                notedState = new HomeControllerState(s1,s2, HomeControllerStates.NOTED_AUCTIONS, this::onNoted, userService.getUser());
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentState.set(notedState);
        });

        Image notedImg = new Image("/img/heart.png");
        SidebarButton notedAuctions = new SidebarButton("Gemerkte Auktionen", notedImg, notedAuctionsBtn);
        btns.add(notedAuctions);

        Button myAuctionsBtn = new Button();
        myAuctionsBtn.setOnAction((ActionEvent event) -> {
            String s1 = "Hallo";
            String s2 = "Das sind deine erstellten Auktionen";
            try {
                myAuctionsState = new HomeControllerState(s1,s2, HomeControllerStates.MY_AUCTIONS, this::onMyAuction, userService.getUser());
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentState.set(myAuctionsState);
        });

        Image myAuctionsImg = new Image("/img/myAuctions.png");
        SidebarButton myAuctions = new SidebarButton("Meine Auktionen", myAuctionsImg, myAuctionsBtn);
        btns.add(myAuctions);

        Button myBidsButton = new Button();
        myBidsButton.setOnAction((ActionEvent event) -> {
            String s1 = "Hallo";
            String s2 = "Darauf hast du geboten";
            try {
                myBidsState = new HomeControllerState(s1,s2, HomeControllerStates.MY_AUCTIONS, this::onMyBids, userService.getUser());
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentState.set(myBidsState);
        });



        Image myBidsImg = new Image("/img/myAuctions.png");
        SidebarButton myBids = new SidebarButton("Meine Gebote", myBidsImg, myBidsButton);
        btns.add(myBids);

        // mySellsBtn
        Button mySellsBtn = new Button();

        try {
            mySellsState = new HomeControllerState("Hallo","Das hast du bereits verkauft", HomeControllerStates.MY_SELLS, this::onMySells, userService.getUser());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mySellsBtn.setOnAction((ActionEvent event) -> {
            currentState.set(mySellsState);
        });
        SidebarButton mySells = new SidebarButton("Meine Verkäufe", null, mySellsBtn);
        btns.add(mySells);

        // ------ //


        Button showOrdersBtn = new Button();
        showOrdersBtn.setOnAction((ActionEvent event) -> {
            try {
                router.route("/showOrder");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        Image showOrderImg = new Image("/img/myAuctions.png");
        SidebarButton showOrders = new SidebarButton("Meine Bestellungen", showOrderImg , showOrdersBtn);
        btns.add(showOrders);


        //voucher

        Button showVouchersBtn = new Button();
        showVouchersBtn.setOnAction((ActionEvent event) -> {
            try {
                router.route("/showVoucher");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        Image showVoucherImg = new Image("/img/myAuctions.png");
        SidebarButton showVoucher = new SidebarButton("Meine Gutscheine", showVoucherImg , showVouchersBtn);
        btns.add(showVoucher);

        return btns;
    }

    private List<SidebarButton> createCommercialCustomerButtons() {
        List<SidebarButton> btns = new ArrayList<>();
        Button lastViewedBtn = new Button();
        lastViewedBtn.setOnAction((ActionEvent event) -> {
            currentState.set(lastViewedState);
        });
        SidebarButton lastViewed = new SidebarButton("Zuletzt angesehen", null, lastViewedBtn);
        btns.add(lastViewed);

        Button myProductsBtn = new Button();
        myProductsBtn.setOnAction((ActionEvent event) -> {
            String s1 = "Hallo";
            String s2 = "Das sind deine erstellten Produkte";
            try {
                notedState = new HomeControllerState(s1,s2, HomeControllerStates.MY_PRODUCTS, this::onMyProducts, userService.getUser());
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentState.set(notedState);
        });



        Button mySellsBtn = new Button();

        try {
            mySellsState = new HomeControllerState("Hallo","Das hast du bereits verkauft", HomeControllerStates.MY_SELLS, this::onMySells, userService.getUser());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mySellsBtn.setOnAction((ActionEvent event) -> {
            currentState.set(mySellsState);
        });
        SidebarButton mySells = new SidebarButton("Meine Verkäufe", null, mySellsBtn);
        btns.add(mySells);

        Image notedImg = new Image("/img/myAuctions.png");
        SidebarButton notedAuctions = new SidebarButton("Erstellte Produkte", notedImg, myProductsBtn);
        btns.add(notedAuctions);


        return btns;
    }

    @Override
    public void onRoute(){
        currentState.get().getStateFunction().run();
        setWelcomeLabel(currentState.get().getLabel());
        navigationController.setSideBar(sidebar);
    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {
        
    }

    public void onShowMore() {
        router.route("/catalog");
    }

    @Override
    public void onLeave() {
        navigationController.clearSideBar();
    }

    public void onLastViewed() {

        ResponseEntity<List<Product>> responseEntity = null;
        try {
            responseEntity = productService.getLastViewed(user.getUserId());
            if(responseEntity.getStatus() == 200) {
                offerList.setAll(responseEntity.getData());
            } else  {
                offerList.setAll(Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            offerList.setAll(Collections.EMPTY_LIST);
            e.printStackTrace();
        }

    }

    public void onNoted() {
        try {
            ResponseEntity<List<Auction>> responseEntity= auctionService.getNotedAuctions();
            if(responseEntity.getStatus() == 200) {
                this.offerList.setAll(responseEntity.getData());
            } else {
                offerList.setAll(Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            offerList.setAll(Collections.EMPTY_LIST);
            e.printStackTrace();
        }
    }

    public void onMySells() {
        try {
            ResponseEntity<List<MySellsDto>> responseEntity= userService.getMySells();
            if(responseEntity.getStatus() == 200) {
                offerList.setAll(responseEntity.getData());
            } else {
                offerList.setAll(Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            offerList.setAll(Collections.EMPTY_LIST);
            e.printStackTrace();
        }
    }

    public void onMyAuction() {
        try {
            ResponseEntity<List<Auction>> responseEntity= auctionService.getMy();
            if(responseEntity.getStatus() == 200) {
                this.offerList.setAll(responseEntity.getData());
            } else {
                offerList.setAll(Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            offerList.setAll(Collections.EMPTY_LIST);
            e.printStackTrace();
        }
    }

    public void onMyBids() {
        ResponseEntity<List<Auction>> responseEntity = null;
        try {
            responseEntity = auctionService.getMyBid();


            if(responseEntity.getStatus() == 200) {
                offerList.setAll(responseEntity.getData());
            } else {
                offerList.setAll(Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            offerList.setAll(Collections.EMPTY_LIST);
            e.printStackTrace();
        }
    }

    public void onMyProducts() {
        try {
            ResponseEntity<List<Product>> responseEntity= productService.getMy();
            logger.log(responseEntity.getStatus());
            if(responseEntity.getStatus() == 200) {
                this.offerList.setAll(responseEntity.getData());
            } else {
                offerList.setAll(Collections.EMPTY_LIST);
            }
        } catch (Exception e) {
            offerList.setAll(Collections.EMPTY_LIST);
            e.printStackTrace();
        }
    }

    private void setUserImg() {
        ImagePattern imagePattern = new ImagePattern(userService.getImage());
        avatar.setFill(imagePattern);
    }

    public void setProductService(ProductService productService) {
        this.productService=productService;
    }

    private void setWelcomeLabel(String text) {
        welcomeLabel.setText(text);
    }

    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;

    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        userService.getUserEvenIfNull().addListener((observableValue, oldval, newval) -> {
            if(newval != null && oldval == null) {
                this.user = newval;
                try {
                    this.handleLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(newval != null) {
                this.user = newval;
                handleUserChange();
            }
        });
    }

    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }



}
