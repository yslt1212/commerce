package de.sep.javafx.component.offercard;

import de.sep.domain.model.Auction;
import de.sep.domain.model.Offer;
import de.sep.domain.model.Product;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;
import javafx.scene.layout.Pane;

public class OfferCard extends Pane {
    Offer offer;
    Logger logger = new Logger(getClass().getCanonicalName());
    AuctionService auctionService;
    public OfferCard(Offer offer, Router router, UserService userService, AuctionService auctionService) {
        try {
            this.offer = offer;
            if(offer instanceof Product) {
               ProductCard productCard = new ProductCard(router, (Product) offer, userService);
               getChildren().add(productCard);
            } else if(offer instanceof Auction) {

                AuctionCard auctionCard = new AuctionCard(router, (Auction) offer, userService, auctionService);

                getChildren().add(auctionCard);
            }

        } catch (Exception e) {

        }
    }
}
