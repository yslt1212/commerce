package de.sep.javafx.controller;

import de.sep.domain.model.Auction;
import de.sep.domain.model.Offer;
import de.sep.domain.model.Product;
import de.sep.domain.model.User;
import de.sep.javafx.component.offercard.OfferCard;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.CatalogFilter;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.apache.commons.collections.map.HashedMap;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CatalogController implements Routable, Initializable {

    @FXML
    HBox offerCardContainer;
    @FXML
    ScrollPane offerScrollPane;
    @FXML
    TextField tfProductsByName;
    @FXML
    TextField tfProductsByCategory;
    @FXML
    ChoiceBox<CatalogFilter.SortingByPrice> sortByPriceChoiceBox;
    @FXML
    ChoiceBox<CatalogFilter.SortingByOrder> sortByOfferChoiceBox;
    @FXML
    ChoiceBox<CatalogFilter.SortingByDistance> sortByDistanceChoiceBox;
    @FXML
    Label cataloginfo;


    Router router;
    ProductService productService;
    UserService userService;
    AuctionService auctionService;
    User user;
    Logger logger = new Logger(getClass().getCanonicalName());
    SimpleListProperty<Product> allProducts;
    SimpleListProperty<Auction> allAuctions;

    SimpleObjectProperty<CatalogFilter> filter;

    public CatalogController(){
        filter = new SimpleObjectProperty<>(new CatalogFilter());
        allProducts = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        allAuctions = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Custom Strings for different sortings
        sortByPriceChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CatalogFilter.SortingByPrice sorting) {
                switch (sorting){
                    case NONE -> {
                        return "Unsortiert (Preis)";
                    }
                    case BY_PRICE_ASC -> {
                        return "Preis aufsteigend";
                    }

                    case BY_PRICE_DESC -> {
                        return "Preis absteigend";
                    }
                    default -> {
                        return "undefined";
                    }
                }
            }
            @Override
            public CatalogFilter.SortingByPrice fromString(String string) {
                System.out.println(string);
                return null;
            }
        });

        sortByDistanceChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CatalogFilter.SortingByDistance sorting) {
                switch (sorting){
                    case NONE -> {
                        return "Unsortiert (Entfernung)";
                    }
                    case BY_DISTANCE_ASC -> {
                        return "nach Entfernung aufsteigend";
                    }

                    case BY_DISTANCE_DESC -> {
                        return "nach Entfernung absteigend";
                    }
                    default -> {
                        return "undefined";
                    }
                }
            }
            @Override
            public CatalogFilter.SortingByDistance fromString(String string) {
                System.out.println(string);
                return null;
            }
        });

        sortByOfferChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CatalogFilter.SortingByOrder sorting) {
                switch (sorting){
                    case BY_ALL_ORDERS -> {
                        return "Alle Angebote";
                    }
                    case BY_AUCTIONS -> {
                        return "Nur Auktionen";
                    }
                    case BY_PRODUCTS -> {
                        return "Nur Sofortkauf-Produkte";
                    }
                    case BY_EXPIRED_ORDERS -> {
                        return "Abgelaufene Auktionen";
                    }
                    default -> {
                        return "undefined";
                    }
                }
            }
            @Override
            public CatalogFilter.SortingByOrder fromString(String string) {
                System.out.println(string);
                return null;
            }

        });
        // Fill Choicebox Items
        sortByPriceChoiceBox.getItems().addAll(CatalogFilter.SortingByPrice.values());
        sortByPriceChoiceBox.getSelectionModel().selectFirst();
        sortByOfferChoiceBox.getItems().addAll(CatalogFilter.SortingByOrder.values());
        sortByOfferChoiceBox.getSelectionModel().selectFirst();
        sortByDistanceChoiceBox.getItems().addAll(CatalogFilter.SortingByDistance.values());
        sortByDistanceChoiceBox.getSelectionModel().selectFirst();


        // Filter Listeners
        sortByDistanceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldSorting, newSorting) -> {
            CatalogFilter catalogFilter = filter.get();
            catalogFilter = catalogFilter.withSortingByDistance(newSorting);
            filter.set(catalogFilter);
        });
        sortByOfferChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldSorting, newSorting) -> {
            CatalogFilter catalogFilter = filter.get();
            catalogFilter = catalogFilter.withSortingByOrder(newSorting);
            filter.set(catalogFilter);
        });
        sortByPriceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldSorting, newSorting) -> {
            CatalogFilter catalogFilter = filter.get();
            catalogFilter = catalogFilter.withSortingByPrice(newSorting);
            filter.set(catalogFilter);
        });
        tfProductsByCategory.textProperty().addListener(((observable, oldCategory, newCategory) -> {
            CatalogFilter catalogFilter = filter.get();
            catalogFilter = catalogFilter.withCategory(newCategory);
            filter.set(catalogFilter);
        }));
        tfProductsByName.textProperty().addListener(((observable, oldQuery, newQuery) -> {
            CatalogFilter catalogFilter = filter.get();
            catalogFilter = catalogFilter.withQuery(newQuery);
            filter.set(catalogFilter);
        }));
        filter.addListener((observable, oldFilter, newFilter) -> {
            updateOfferCards(filterOffers(newFilter, allProducts.get(), allAuctions.get()));
        });
        // Products/Auctions Listener
        allProducts.addListener((observable, oldProducts, newProducts) -> {
            updateOfferCards(filterOffers(filter.get(), newProducts, allAuctions.get()));
        });
        allAuctions.addListener((observable, oldAuctions, newAuctions) -> {
            updateOfferCards(filterOffers(filter.get(), allProducts.get(), newAuctions));
        });
    }

    private void updateOfferCards(List<Offer> offers){
        offerCardContainer.getChildren().clear();
        offers.forEach(offer -> {
            try {
                OfferCard offerCard = new OfferCard(offer, router, userService, auctionService);
                offerCardContainer.getChildren().add(offerCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<Offer> filterOffers(CatalogFilter filter, List<Product> products, List<Auction> auctions ){
        List<Offer> filteredOffers = new ArrayList<>();
        Map <Integer,Double> distanceMap = new HashMap<>();

        switch (filter.getSortingByOrder()){
            case BY_ALL_ORDERS -> {
                filteredOffers.addAll(products);
                filteredOffers.addAll(auctions);
            }
            case BY_AUCTIONS -> {
                filteredOffers.addAll(auctions);
            }
            case BY_PRODUCTS -> {
                filteredOffers.addAll(products);
            }
            case BY_EXPIRED_ORDERS -> {
                try {
                    ResponseEntity<List<Auction>> responseEntity = auctionService.getAllExpiredAuctions();
                    if (responseEntity.getStatus() == 200) filteredOffers.addAll(responseEntity.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        if(filter.getCategory().length() != 0){
            filteredOffers = filteredOffers.stream().filter(product -> product.getCategory().toLowerCase().contains(filter.getCategory().toLowerCase())).collect(Collectors.toList());
        }
        if(filter.getQuery().length() != 0){
            filteredOffers = filteredOffers.stream().filter(product -> product.getOffername().toLowerCase().startsWith(filter.getQuery().toLowerCase())).collect(Collectors.toList());
        }
       switch (filter.getSortingByPrice()){
            case NONE -> {
            }
            case BY_PRICE_ASC -> {
                filteredOffers.sort( (offer1, offer2) -> {
                    if (offer1 instanceof Product && offer2 instanceof Product) return (int) Math.signum(((Product) offer1).getPrice() - ((Product) offer2).getPrice());
                    if (offer1 instanceof Product && offer2 instanceof Auction) return (int) Math.signum(((Product) offer1).getPrice() - ((Auction) offer2).getBidAmount());
                    if (offer1 instanceof Auction && offer2 instanceof Product) return (int) Math.signum(((Auction) offer1).getBidAmount() - ((Product) offer2).getPrice());
                    if (offer1 instanceof Auction && offer2 instanceof Auction) return (int) Math.signum(((Auction) offer1).getBidAmount() - ((Auction) offer2).getBidAmount());
                    return 0;
                });
            }
            case BY_PRICE_DESC -> {
              filteredOffers.sort( (offer1, offer2) -> {
               if (offer1 instanceof Product && offer2 instanceof Product) return (int)Math.signum(((Product) offer2).getPrice() - ((Product) offer1).getPrice());
               if (offer1 instanceof Product && offer2 instanceof Auction) return (int)Math.signum(((Auction) offer2).getBidAmount() - ((Product) offer1).getPrice());
               if (offer1 instanceof Auction && offer2 instanceof Auction) return (int)Math.signum(((Auction) offer2).getBidAmount() - ((Auction) offer1).getBidAmount());
               if (offer1 instanceof Auction && offer2 instanceof Product) return (int)Math.signum(((Product) offer2).getPrice() - ((Auction) offer1).getBidAmount());
               return 0;
              });
            }
        }
        switch (filter.getSortingByDistance()){
            case NONE -> {
            }
            case BY_DISTANCE_ASC -> {
                double distance = 0;
                try {
                    for(int i=0; i<filteredOffers.size();i++){
                        distance = userService.getdistance(user.getUserId(), filteredOffers.get(i).getSeller()).getData();
                        distanceMap.put(filteredOffers.get(i).getSeller(),distance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                filteredOffers.sort( (offer1, offer2) -> {
                            return (int) Math.signum(distanceMap.get(offer1.getSeller())-distanceMap.get(offer2.getSeller()));
                        });

                System.out.println("Distanz aufsteigend");
            }
            case BY_DISTANCE_DESC -> {
                double distance = 0;
                try {
                    for(int i=0; i<filteredOffers.size();i++){
                        distance = userService.getdistance(user.getUserId(), filteredOffers.get(i).getSeller()).getData();
                        distanceMap.put(filteredOffers.get(i).getSeller(),distance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                filteredOffers.sort( (offer1, offer2) -> {
                    return (int) Math.signum(distanceMap.get(offer2.getSeller())-distanceMap.get(offer1.getSeller()));
                });
                System.out.println("Distanz absteigend");
            }

        }
        return filteredOffers;
    }


    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute()  {
        try {
            user = userService.getUser().get();
            ResponseEntity<List<Product>> listproducts = productService.getAllProducts();
            ResponseEntity<List<Auction>> listauctions= auctionService.getAllAuctions();
            cataloginfo.setVisible(false);
            if (listproducts == null || listauctions == null){
                cataloginfo.setVisible(true);
                cataloginfo.setText("No answer of backend");
                return;
            }
            if (listproducts.getStatus() == 200) allProducts.setAll(listproducts.getData());
            else allProducts.setAll();
            if (listauctions.getStatus() == 200) allAuctions.setAll(listauctions.getData());
            else allAuctions.setAll();
            if (listproducts.getStatus() != 200 && listauctions.getStatus() != 200){
                cataloginfo.setVisible(true);
                cataloginfo.setText("No data in database");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }

    @Override
    public void onLeave() {
        reset();
    }
    private void reset() {
        filter.get().setQuery("");
        filter.get().setCategory("");
        filter.get().setSortingByDistance(CatalogFilter.SortingByDistance.NONE);
        filter.get().setSortingByPrice(CatalogFilter.SortingByPrice.NONE);
        filter.get().setSortingByOrder(CatalogFilter.SortingByOrder.BY_ALL_ORDERS);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    public void setAuctionService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }
}
