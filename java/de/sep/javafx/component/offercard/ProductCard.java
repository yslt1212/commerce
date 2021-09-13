package de.sep.javafx.component.offercard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.sep.domain.model.Product;
import de.sep.domain.model.User;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.Logger;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class ProductCard extends Pane {

    @FXML Label productName;

    @FXML Label price;

    @FXML Label seller;

    @FXML Label category;

    @FXML Label distance;

    @FXML TextArea description;

    @FXML Label info;

    @FXML Label oldprice;

    @FXML Label oldpricetext;

    @FXML Label discount;

    @FXML Label pricetext;

    @FXML Button viewProductButton;

    @FXML Button buyButton;

    Router router;
    Product product;
    UserService userService;
    User user;
    Logger logger = new Logger(getClass().getCanonicalName());
    public ProductCard(Router router, Product product, UserService userService) throws Exception {
        this.router = router;
        this.product = product;
        this.userService = userService;
        this.user = userService.getUser().get();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/productCard.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);

        try {
            fxml.load();
            info.setVisible(false);

            if (this.user.getUserId() != this.product.getSeller()){
                ResponseEntity<Double> response =userService.getdistance(this.user.getUserId(), this.product.getSeller());
                logger.log(response.getStatus(),response.getMessage());
                distance.setText(Double.toString(response.getData())+" km");
            } else distance.setText("0 km");

            int usertype = user.getType();
            if(usertype == 1) {
                buyButton.setVisible(false);
                buyButton.setFocusTraversable(false);
            } else {
                buyButton.setVisible(true);
                buyButton.setFocusTraversable(true);
            }
            if(user.getUserId() == product.getSeller()){
                viewProductButton.setText("Bearbeiten");
                info.setText("Ihr eigenes Angebot");
                info.setVisible(true);
            }
            ResponseEntity<Double> re= userService.getOldPrice(product.getOfferId());
            double alterpreis = -100;
            if (re.getStatus() == 200) {
                alterpreis = re.getData();
                if (alterpreis > product.getPrice()) {
                    oldprice.setVisible(true);
                    oldpricetext.setVisible(true);
                    oldprice.setText(Double.toString(alterpreis));
                    discount.setVisible(true);
                    int rabatt = ((int)Math.round((1-(product.getPrice()/alterpreis))*100));
                    discount.setText("- "+ rabatt +"%"+ " Rabatt");
                    pricetext.setText("neuer Preis");
                }
            }
            setProductName(product.getOffername());
            setSeller(userService.getSeller(this.product.getSeller()).getData().getCompanyName());
            setCategory(product.getCategory());
            setPrice(Double.toString(product.getPrice()));
            setDescription(product.getDescription());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void buyProduct() throws Exception {

        ResponseEntity<Double> responseEntity =  userService.buyProduct(product.getProductId());
        if(responseEntity.getStatus() == 200) {
            userService.updateUser();
            router.route("/rating",Map.of("productId",Integer.toString(product.getProductId())) );
        } else {
            info.setText(responseEntity.getMessage());
            info.setVisible(true);
        }
    }

    public void showProduct() throws Exception {
        if(user.getUserId() == product.getSeller()){
            router.route("/product/edit", Map.of("productid",Integer.toString(product.getProductId())));
        }else {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("productId", Integer.toString(product.getProductId()));
            params.put("productName", product.getOffername());
            params.put("category", product.getCategory());
            params.put("price", Double.toString(product.getPrice()));
            params.put("seller", Integer.toString(product.getSeller()));
            params.put("description", product.getDescription());
            router.route("/showProduct", params);
        }
    }

    public void setProductName(String t) {
        productName.setText(t);
    }

    public void setPrice(String t) {
        price.setText(t);
    }

    public void setSeller(String sellerName) {
        seller.setText(sellerName);
    }

    public void setCategory(String t) {
        category.setText(t);
    }

    public void setDescription(String t) {
        description.setText(t);
    }
}
