package de.sep.javafx;

import de.sep.javafx.controller.*;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Route;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.*;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.eclipse.jetty.client.HttpClient;

public class AppController extends Application {

    public AppController() throws IOException {}

    @Override
    public void start(Stage primaryStage) throws Exception {

        // We set the size of the screen to always be fullscreen and to hide the bar
        primaryStage.setTitle("Amazonas");
        primaryStage.setMaximized(true);
        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getBounds();
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Instantiating the Authentication Service
        HttpClient httpClient = new HttpClient();
        AuthenticationService authenticationService = new AuthenticationService(httpClient);
        UserService userService = new UserService(authenticationService, httpClient);
        ProductService productService = new ProductService(authenticationService, httpClient);
        AuctionService auctionService = new AuctionService(httpClient);
        RatingService ratingService = new RatingService(authenticationService,httpClient);
        MessagingService messagingService = new MessagingService(httpClient);
        ImageService imageService = new ImageService();

        // Loading the fxml of all Screens we need in our application
        FXMLLoader loginFxml = new FXMLLoader(getClass().getResource("/login.fxml"));
        FXMLLoader registerFxml = new FXMLLoader(getClass().getResource("/register.fxml"));
        FXMLLoader forgotPasswordFxml = new FXMLLoader(getClass().getResource("/forgotPassword.fxml"));
        FXMLLoader navigationFxml = new FXMLLoader(getClass().getResource("/navigation.fxml"));
        FXMLLoader homeFxml = new FXMLLoader(getClass().getResource("/home.fxml"));
        FXMLLoader walletFxml = new FXMLLoader(getClass().getResource("/chargeWallet.fxml"));
        FXMLLoader productFxml = new FXMLLoader(getClass().getResource("/addProduct.fxml"));
        FXMLLoader catalogFxml = new FXMLLoader(getClass().getResource("/catalog.fxml"));
        FXMLLoader editProfileFxml = new FXMLLoader(getClass().getResource("/editProfile.fxml"));
        FXMLLoader showProductFxml = new FXMLLoader(getClass().getResource("/showProduct.fxml"));
        FXMLLoader showOrderFxml = new FXMLLoader(getClass().getResource("/showOrder.fxml"));
        FXMLLoader addAuction = new FXMLLoader(getClass().getResource("/addAuction.fxml"));
        FXMLLoader changeProductFxml = new FXMLLoader(getClass().getResource("/changeProduct.fxml"));
        FXMLLoader bidOnAuctionFxml = new FXMLLoader(getClass().getResource("/bidOnAuction.fxml"));
        FXMLLoader showProfileFxml = new FXMLLoader(getClass().getResource("/showProfile.fxml"));
        FXMLLoader ratingFxml = new FXMLLoader(getClass().getResource("/rating.fxml"));
        FXMLLoader voucherGiftFxml = new FXMLLoader(getClass().getResource("/voucherGift.fxml"));
        FXMLLoader conversationsFxml = new FXMLLoader(getClass().getResource("/conversations.fxml"));
        FXMLLoader chatFxml = new FXMLLoader(getClass().getResource("/chat.fxml"));
        FXMLLoader showVoucherFxml = new FXMLLoader(getClass().getResource("/showVouchers.fxml"));

        // Creating a scene out of the fxml for all fxmls
        // We need to do this for all scenes cause the Controller only gets initiated on scene
        // creation...



        Scene loginScene = new Scene(loginFxml.load());
        Scene registerScene = new Scene(registerFxml.load());
        Scene forgotPasswordScene = new Scene(forgotPasswordFxml.load());
        Scene navigationScene = new Scene(navigationFxml.load());
        Scene homeScene = new Scene(homeFxml.load());
        Scene walletScene = new Scene(walletFxml.load());
        Scene productScene = new Scene(productFxml.load());
        Scene catalogScene = new Scene(catalogFxml.load());
        Scene editProfileScene = new Scene(editProfileFxml.load());
        Scene showProductScene = new Scene(showProductFxml.load());
        Scene showOrderScene = new Scene(showOrderFxml.load());
        Scene addAuctionScene = new Scene(addAuction.load());
        Scene changeProductScene = new Scene(changeProductFxml.load());
        Scene bidOnAuctionScene = new Scene(bidOnAuctionFxml.load());
        Scene showProfileScene = new Scene(showProfileFxml.load());
        Scene ratingScene = new Scene(ratingFxml.load());
        Scene sendVoucherScene= new Scene(voucherGiftFxml.load());
        Scene conversationsScene = new Scene(conversationsFxml.load());
        Scene chatScene = new Scene(chatFxml.load());
        Scene showVoucherScene = new Scene(showVoucherFxml.load());

        loginScene.getStylesheets().add("css/root.css");

        // Getting all Controllers
        LoginController loginController = loginFxml.getController();
        RegisterController registerController = registerFxml.getController();
        ForgotPasswordController forgotPasswordController = forgotPasswordFxml.getController();
        NavigationController navigationController = navigationFxml.getController();
        WalletController walletController = walletFxml.getController();
        ProductController productController = productFxml.getController();
        HomeController homeController= homeFxml.getController();
        CatalogController catalogController= catalogFxml.getController();
        EditProfileController editProfileController = editProfileFxml.getController();
        ShowProductController showProductController = showProductFxml.getController();
        ShowOrderController showOrderController = showOrderFxml.getController();
        AddAuctionController addAuctionController = addAuction.getController();
        ChangeProductController changeProductController = changeProductFxml.getController();
        BidOnAuctionController bidOnAuctionController = bidOnAuctionFxml.getController();
        RatingController ratingController = ratingFxml.getController();
        ShowProfileController showProfileController = showProfileFxml.getController();
        SendVoucherController sendVoucherController = voucherGiftFxml.getController();
        ConversationsController conversationsController = conversationsFxml.getController();
        ChatController chatController = chatFxml.getController();
        VoucherController showVoucherController = showVoucherFxml.getController();





        // Creating an instance of our router class
        Router router = new Router(primaryStage);

        // Creating Routes

        Route loginRoute = new Route("/login", loginScene, true);
        Route registerRoute = new Route("/register", registerScene, true);
        Route forgotPasswordRoute = new Route("/forgotPassword", forgotPasswordScene, true);
        Route navigationRoute = new Route("/navigation", navigationScene,navigationController, true);
        Route homeRoute = new Route("/home", homeScene,homeController, false);
        Route walletRoute = new Route("/user/wallet", walletScene, walletController, false);
        Route productRoute = new Route("/product", productScene,false);
        Route catalogRoute = new Route("/catalog", catalogScene,catalogController, false);
        Route editProfileRoute = new Route("/user/edit",editProfileScene,editProfileController,false);
        Route showProductRoute=new Route("/showProduct", showProductScene, showProductController, false);
        Route showOrderRoute =  new Route("/showOrder", showOrderScene,showOrderController, false);
        Route addAuctionRoute=new Route("/addAuction", addAuctionScene,addAuctionController, false);
        Route changeProductRoute = new Route("/product/edit", changeProductScene,changeProductController, false);
        Route bidOnAuctionRoute=new Route("/bidOnAuction", bidOnAuctionScene,bidOnAuctionController, false);
        Route showProfileRoute = new Route("/showProfile",showProfileScene , showProfileController, false);
        Route ratingRoute = new Route("/rating", ratingScene,ratingController, false);
        Route sendVoucherRoute = new Route("/sendVoucher", sendVoucherScene,sendVoucherController, false);
        Route conversationsRoute = new Route("/conversations", conversationsScene, conversationsController, false);
        Route chatRoute = new Route("/chat", chatScene, chatController, false);

        Route showVoucherRoute = new Route("/showVoucher", showVoucherScene, showVoucherController, false);


        // Adding Routes
        router.addRoute(loginRoute);
        router.addRoute(registerRoute);
        router.addRoute(forgotPasswordRoute);
        router.addRoute(navigationRoute);
        router.addRoute(homeRoute);
        router.addRoute(walletRoute);
        router.addRoute(productRoute);
        router.addRoute(catalogRoute);
        router.addRoute(editProfileRoute);
        router.addRoute(showProductRoute);
        router.addRoute(showOrderRoute);
        router.addRoute(addAuctionRoute);
        router.addRoute(changeProductRoute);
        router.addRoute(bidOnAuctionRoute);
        router.addRoute(showProfileRoute);
        router.addRoute(ratingRoute);
        router.addRoute(conversationsRoute);
        router.addRoute(chatRoute);
        router.addRoute(showVoucherRoute);

        router.addRoute(sendVoucherRoute);

        // Passing the router to each Controller which needs to route
        loginController.setRouter(router);
        registerController.setRouter(router);
        forgotPasswordController.setRouter(router);
        navigationController.setRouter(router);
        walletController.setRouter(router);
        editProfileController.setRouter(router);
        homeController.setRouter(router);
        catalogController.setRouter(router);
        showProductController.setRouter(router);
        showOrderController.setRouter(router);
        addAuctionController.setRouter(router);
        changeProductController.setRouter(router);
        bidOnAuctionController.setRouter(router);
        ratingController.setRouter(router);
        showProfileController.setRouter(router);
        productController.setRouter(router);
        sendVoucherController.setRouter(router);
        conversationsController.setRouter(router);
        chatController.setRouter(router);
        showVoucherController.setRouter(router);



        // Passing the AuthenticationService to each Controller which needs it
        showProfileController.setUserService(userService);
        showProfileController.setRatingService(ratingService);
        ratingController.setUserService(userService);
        ratingController.setProductService(productService);
        ratingController.setRatingService(ratingService);
        showProductController.setProductService(productService);
        showOrderController.setUserService(userService);
        showVoucherController.setUserService(userService);
        sendVoucherController.setUserService(userService);

        loginController.setAuthenticationService(authenticationService);
        registerController.setAuthenticationService(authenticationService);
        editProfileController.setAuthenticationService(authenticationService);

        homeController.setUserService(userService);
        homeController.setProductService(productService);
        productController.setProductService(productService);
        changeProductController.setProductService(productService);

        navigationController.setUserService(userService);
        walletController.setUserService(userService);
        editProfileController.setUserService(userService);
        catalogController.setProductService(productService);
        catalogController.setUserService(userService);
        catalogController.setAuctionService(auctionService);
        showProductController.setUserService(userService);
        showOrderController.setProductService(productService);

        addAuctionController.setUserService(userService);

        homeController.setNavigationController(navigationController);
        homeController.setAuctionService(auctionService);
        addAuctionController.setAuctionService(auctionService);
        bidOnAuctionController.setAuctionService(auctionService);
        bidOnAuctionController.setUserService(userService);
        // later he wont need this cause we can identify user through token of authservice
        productController.setUserService(userService);

        // conversationController dependency injection
        conversationsController.setImageService(imageService);
        conversationsController.setMessagingService(messagingService);
        conversationsController.setUserService(userService);
        // chatController dependency injection
        chatController.setMessagingService(messagingService);
        chatController.setUserService(userService);
        chatController.setImageService(imageService);

        // Setting the scene into the stage
        primaryStage.setScene(loginScene);

        // Showing the stage
        primaryStage.show();
    }

    public static void launchController(String[] args) {
        Application.launch(args);
    }
}
