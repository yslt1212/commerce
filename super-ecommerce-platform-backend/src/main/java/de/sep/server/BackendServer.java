package de.sep.server;

import de.sep.server.adapter.TelegramBotAdapter;
import de.sep.server.controller.*;
import de.sep.server.controller.doubleAuth.DoubleAuthController;
import de.sep.server.worker.AuctionConclusionWorker;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.servlet.Servlet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackendServer {

    public static void main(String[] args) throws Exception {



        Server server = new Server(8080);
        SessionHandler sessionHandler = new SessionHandler();
        ServletContextHandler handler = new ServletContextHandler(server, "/");

        // user
        // Implements getById and put
        handler.addServlet(UsersController.class, "/users");
        handler.addServlet(SellerController.class, "/users/seller");
        handler.addServlet(LoginController.class, "/user/login");
        handler.addServlet(LogoutController.class, "/user/logout");
        handler.addServlet(RegisterController.class, "/user/register");
        handler.addServlet(RatingController.class, "/rating");
        handler.addServlet(WalletController.class, "/user/wallet");
        handler.addServlet(ChangePasswordController.class, "/user/changePassword");
        handler.addServlet(AverageRatingController.class, "/user/averageRating");
        handler.addServlet(BidController.class, "/user/auctions/bid");
        handler.addServlet(MySellsController.class, "/user/mySells");
        handler.addServlet(MyAuctionsController.class, "/user/auctions/my");
        handler.addServlet(NoteAuctionController.class, "/user/auctions/note");
        handler.addServlet(DoubleAuthController.class, "/user/doubleAuth");
        handler.addServlet(VoucherController.class, "/voucher");
        handler.addServlet(SendGiftToController.class, "/user/sendGiftTo");
        handler.addServlet(GeocodingController.class,"/user/geocoding");
        handler.addServlet(MessageController.class, MessageController.PATH);
        handler.addServlet(ConversationController.class, ConversationController.PATH);
        handler.addServlet(myVoucherController.class,"/user/voucher");

        handler.addServlet(OrderController.class, "/user/products/order");
        handler.addServlet(OrdersController.class, "/user/orders");
        handler.addServlet(MyProductController.class, "/user/products/my");
        handler.addServlet(LastViewedController.class, "/user/products/lastViewed");

        // products
        handler.addServlet(ProductController.class, "/products");
        handler.addServlet(ProductHistoryController.class, "/producthistory");
        handler.addServlet(SimiliarProductController.class, "/products/similiar");


        // auctions
        handler.addServlet(AuctionController.class, "/auctions");
        handler.addServlet(AuctionImgController.class, "/auctions/img");
        handler.addServlet(ExpiredAuctionController.class, "/auctions/expired");
        handler.addServlet(HighestBidderController.class, "/auctions/highestbidder");
        handler.addServlet(CoordinatesController.class,"/auctions/coordinates");

        // Start Workers
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        AuctionConclusionWorker auctionConclusionWorker = new AuctionConclusionWorker();
        scheduler.scheduleAtFixedRate(auctionConclusionWorker,0, 20, TimeUnit.SECONDS);

        sessionHandler.setHandler(handler);
        server.setHandler(sessionHandler);

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new TelegramBotAdapter());
        } catch (TelegramApiException e) {
            System.out.println("Telegram error");
            e.printStackTrace();
        }

        server.start();
        server.join();


    }
}
