package de.sep.javafx.controller;

import de.sep.domain.model.Offer;
import de.sep.domain.model.Product;
import de.sep.domain.model.User;
import de.sep.domain.model.UserType;
import de.sep.javafx.component.offercard.OfferCard;
import de.sep.domain.dto.GetSellerDto;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.AuctionService;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

import java.util.*;

import static java.lang.Thread.sleep;

public class ShowProductController implements Routable {

    UserService userService;
    ProductService productService;
    AuctionService auctionService;
    Router router;

    int productId;
    double pricePrice;
    Product product;

    @FXML
    Label greetLabel;

    @FXML

    Label productNameLabel;

    @FXML
    Label priceLabel;

    @FXML
    Label sellerLabel;

    @FXML
    Label categoryLabel;

    @FXML

    Button buyButton;

    @FXML
    Label linkLabel;

    @FXML
    TextArea descriptionField;

    @FXML

    HBox productCardContainer;

    @FXML

    Label infoLabel;

    @FXML
    Label bewertungsLabel;

    List<Offer> offerlist;


    public ShowProductController() throws Exception {

        offerlist=new ArrayList<>();

    }



    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute() {

    }



    @Override
    public void onRouteWithParams(Map<String, String> params) {
        User user = null;
        Logger logger = new Logger(getClass().getCanonicalName());
        reseToStart();
        try {
            user = userService.getUser().get();
            buyButton.setVisible(user.getType() != UserType.COMMERCIAL);
            userService.viewProduct(Integer.parseInt(params.get("productId")));
            this.productId = Integer.parseInt(params.get("productId"));

            product = new Product();
            product.setOffername(params.get("productName"));
            product.setCategory(params.get("category"));
            product.setPrice(Double.parseDouble(params.get("price")));
            product.setSeller(Integer.parseInt(params.get("seller")));
            product.setDescription(params.get("description"));

            productNameLabel.setText(product.getOffername());
            categoryLabel.setText(product.getCategory());
            priceLabel.setText(params.get("price"));
            sellerLabel.setText(params.get("seller"));
            descriptionField.setText(product.getDescription());
            pricePrice = Double.parseDouble(params.get("price"));
            try {
                bewertungsLabel.setText(Float.toString(userService.getAverageRating(product.getSeller()).getData()));
            }catch(Exception e){
                bewertungsLabel.setText("Bisher nicht bewertet");
            }
            GetSellerDto getSellerDto = userService.getSeller(product.getSeller()).getData();
            linkLabel.setText("Verkäufer: " + getSellerDto.getCompanyName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        offerlist.clear();
        try {
            ResponseEntity<List<Product>> response = productService.getOtherProduct(productId);
            logger.log(response.getStatus(), response.getMessage());
            if (response.getStatus() == 200) {
                offerlist.addAll(response.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        productCardContainer.getChildren().clear();
        for (Offer offer : offerlist) {
            OfferCard offerCard = new OfferCard(offer, router, userService, auctionService);
            productCardContainer.getChildren().add(offerCard);
        }
    }

    public void onBuyPressed() {
        try {
            ResponseEntity<Double> responseEntity = userService.buyProduct(this.productId);

            if(responseEntity.getStatus() == 200) {
                userService.updateUser();
                infoLabel.setVisible(true);
                infoLabel.setText("Produkt gekauft! Danke für den Einkauf!");
                sleep(2000);
                router.route("/rating",Map.of("productId",Integer.toString(productId)));
            } else {
                infoLabel.setText(responseEntity.getMessage());
            }

            infoLabel.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onLinkPressed(){
        router.route("/showProfile",Map.of("userId",String.valueOf(product.getSeller())));

    }


    private void reseToStart() {
        infoLabel.setVisible(false);
        infoLabel.setText("");
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onLeave() {

    }

    public void setProductService(ProductService productService) {this.productService=productService;
    }
    public void setAuctionService(AuctionService auctionService) {this.auctionService = auctionService;
    }

}
