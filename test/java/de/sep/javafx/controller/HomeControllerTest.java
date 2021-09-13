package de.sep.javafx.controller;

import de.sep.domain.model.Auction;
import de.sep.domain.model.Product;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.AuthenticationService;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.util.ResponseEntity;
import javafx.scene.layout.HBox;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class HomeControllerTest {
    HBox productCardContainer = new HBox();
    @InjectMocks
    HomeController homeController;
    AuctionService auctionService;
    ProductService productService;
    AuthenticationService authenticationService;
    HttpClient httpClient;
    List<Auction> auctions = new ArrayList<>();
    List<Product> products = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        httpClient = new HttpClient();
        auctions.add(new Auction(1, new Timestamp(0), 20.0, 20.0));
        auctions.add(new Auction(2, new Timestamp(0), 20.0, 20.0));
        auctions.add(new Auction(3, new Timestamp(0), 20.0, 20.0));
        auctions.add(new Auction(4, new Timestamp(0), 20.0, 20.0));
        products.add(new Product(1, "test", 1, "de", "", "", 1, 1, 2.0));
        products.add(new Product(2, "test", 1, "de", "", "", 1, 2, 2.0));
        products.add(new Product(3, "test", 1, "de", "", "", 1, 3, 2.0));
        products.add(new Product(4, "test", 1, "de", "", "", 1, 4, 2.0));
        homeController = new HomeController();
        auctionService = mock(AuctionService.class);
        productService = mock(ProductService.class);
        authenticationService = mock(AuthenticationService.class);
        homeController.setAuctionService(auctionService);
        homeController.setProductService(productService);
        homeController.productCardContainer = productCardContainer;
    }

    // OnMyBid

    @Test
    public void onMyBidWhenResponseNot200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(404);
        when(auctionService.getMyBid()).thenReturn(responseEntity);
        homeController.onMyBids();
        assertTrue(homeController.offerList.get().isEmpty());
    }

    @Test
    public void onMyBidWhenResponse200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Auction>>();
        responseEntity.setStatus(200);
        responseEntity.setData(auctions);
        when(auctionService.getMyBid()).thenReturn(responseEntity);

        homeController.onMyBids();
        assertTrue(homeController.offerList.get().size() == 4);
    }

    @Test
    public void onMyBidWhenResponse200WithoutDataTest() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Auction>>();
        responseEntity.setStatus(200);
        when(auctionService.getMyBid()).thenReturn(responseEntity);

        homeController.onMyBids();
        assertTrue(homeController.offerList.get().size() == 0);
    }

    // ------------------------------------------------------------------------------ //
    // onMyAuction
    @Test
    public void onMyAuctionWhenResponseNot200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(404);
        when(auctionService.getMy()).thenReturn(responseEntity);



        homeController.onMyAuction();
        assertTrue(homeController.offerList.get().isEmpty());
    }

    @Test
    public void onMyAuctionWhenResponse200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Auction>>();
        responseEntity.setStatus(200);
        responseEntity.setData(auctions);
        when(auctionService.getMy()).thenReturn(responseEntity);

        homeController.onMyAuction();
        assertTrue(homeController.offerList.get().size() == 4);
    }

    @Test
    public void onMyAuctionWhenResponse200WithoutDataTest() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Auction>>();
        responseEntity.setStatus(200);
        when(auctionService.getMy()).thenReturn(responseEntity);

        homeController.onMyAuction();
        assertTrue(homeController.offerList.get().size() == 0);
    }

    // ------------------------------------------------------------------------------ //

    // onNoted
    @Test
    public void onNotedWhenResponseNot200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(404);
        when(auctionService.getNotedAuctions()).thenReturn(responseEntity);



        homeController.onNoted();
        assertTrue(homeController.offerList.get().isEmpty());
    }

    @Test
    public void onNotedWhenResponse200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Auction>>();
        responseEntity.setStatus(200);
        responseEntity.setData(auctions);
        when(auctionService.getNotedAuctions()).thenReturn(responseEntity);

        homeController.onNoted();
        assertTrue(homeController.offerList.get().size() == 4);
    }

    @Test
    public void onNotedWhenResponse200WithoutDataTest() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Auction>>();
        responseEntity.setStatus(200);
        when(auctionService.getNotedAuctions()).thenReturn(responseEntity);

        homeController.onNoted();
        assertTrue(homeController.offerList.get().size() == 0);
    }

    // ------------------------------------------------------------------------------ //

    // onMyProducts
    @Test
    public void onMyProductsWhenResponseNot200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(404);
        when(productService.getMy()).thenReturn(responseEntity);
        homeController.onMyProducts();
        assertTrue(homeController.offerList.get().isEmpty());
    }

    @Test
    public void onMyProductsWhenResponse200Test() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Product>>();
        responseEntity.setStatus(200);
        responseEntity.setData(products);
        when(productService.getMy()).thenReturn(responseEntity);

        homeController.onMyProducts();
        assertTrue(homeController.offerList.get().size() == 4);
    }

    @Test
    public void onMyProductsWhenResponse200WithoutDataTest() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<List<Auction>>();
        responseEntity.setStatus(200);
        when(auctionService.getNotedAuctions()).thenReturn(responseEntity);

        homeController.onMyProducts();
        assertTrue(homeController.offerList.get().size() == 0);
    }

    // ------------------------------------------------------------------------------ //


}
