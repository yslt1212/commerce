package de.sep.javafx.controller;

import de.sep.domain.dto.AddRatingDto;
import de.sep.domain.dto.GetProductDto;
import de.sep.domain.dto.GetSellerDto;
import de.sep.domain.mapper.ProductMapper;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.services.RatingService;
import de.sep.javafx.services.UserService;
import de.sep.domain.model.Product;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Map;

public class RatingController implements Routable {

    Router router;
    @FXML
    Label descriptionLabel;
    @FXML
    Label linkLabel;
    @FXML
    Button sendButton;
    @FXML
    Rectangle ImgField;
    @FXML
    Label sellerLabel;
    @FXML
    TextField ratedNumber;
    @FXML
    TextArea ratedText;
    @FXML
    Label productNameLabel;

    ProductService productService;
    UserService userService;
    RatingService ratingService;

    ProductMapper productMapper = new ProductMapper();

    Product product;

    GetSellerDto buyer;

    Logger logger = new Logger(getClass().getCanonicalName());
    @Override
    public void setRouter(Router router) {
        this.router = router;
    }



    @Override
    public void onRoute() {


    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {
        try {

            if(params.get("productId") != null) {
                ResponseEntity<GetProductDto> response = productService.getById(Integer.parseInt(params.get("productId")));
                if(response.getStatus() == 200){
                    this.product = productMapper.mapGetProductDto(response.getData());
                }

                descriptionLabel.setText(product.getDescription());
                productNameLabel.setText(product.getOffername());
                ResponseEntity<GetSellerDto> sellerResponse = userService.getSeller(product.getSeller());
                if(sellerResponse.getStatus() == 200){
                    GetSellerDto seller = sellerResponse.getData();
                    sellerLabel.setText("Verkäufer:"+seller.getCompanyName());
                }
                if(product.getImg() != null) {
                    byte[] decodedBytes = Base64.getDecoder().decode(product.getImg());
                    ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
                    Image img = new Image(bis);


                    ImgField.setFill(new ImagePattern(img));
                }
            } else {
                ResponseEntity<GetSellerDto> responseEntity = userService.getSeller(Integer.parseInt(params.get("buyerid")));
                buyer = responseEntity.getData();
                descriptionLabel.setText("");
                productNameLabel.setText("");
                sellerLabel.setText("Nutzername:"+ buyer.getUsername());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLeave() {

    }


    public void setUserService(UserService userService) {
        this.userService=userService;
    }
    public void setProductService(ProductService productService) {
        this.productService=productService;
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public void sendRating() {

        try {
            AddRatingDto adto;
            if(buyer != null) {

                adto = new AddRatingDto(userService.getUser().get().getUserId(), buyer.getUserId(), Integer.parseInt(ratedNumber.getText()),ratedText.getText());
            } else {
                adto = new AddRatingDto(userService.getUser().getValue().getUserId(),product.getSeller(),Integer.parseInt(ratedNumber.getText()),ratedText.getText());

            }
            ratingService.addRating(adto);

            router.route("/home");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void onLinkPressed() {

        if(buyer != null) {
            router.route("/showProfile",Map.of("userId",String.valueOf(buyer.getUserId())));
        } else if (product != null){
            router.route("/showProfile",Map.of("userId",String.valueOf(product.getProductId())));
        }

    }
}
