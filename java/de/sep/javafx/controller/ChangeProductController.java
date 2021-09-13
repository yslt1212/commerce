package de.sep.javafx.controller;

import de.sep.domain.model.Product;
import de.sep.domain.dto.GetProductDto;
import de.sep.domain.mapper.ProductMapper;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ProductService;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.image.ImageView;
import org.eclipse.jetty.http.HttpStatus;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ChangeProductController implements Routable, Initializable {

    private ProductService productService;
    private ProductMapper productMapper;
    private Router router;

    SimpleObjectProperty<Product> productProperty;

    @FXML
    public ImageView imageField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField categoryField;
    @FXML
    public TextField priceField;
    @FXML
    public TextArea descriptionField;
    @FXML
    public Toggle deliveryTypePickupToggle;
    @FXML
    public Toggle deliveryTypeDeliveryToggle;
    @FXML
    public Button applyButton;

    public ChangeProductController(){
        productMapper = new ProductMapper();
        productProperty = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productProperty.addListener((observable, oldProduct, newProduct) -> {
            nameField.setText(newProduct.getOffername());
            categoryField.setText(newProduct.getCategory());
            priceField.setText(String.format("%.2f",newProduct.getPrice()));
            descriptionField.setText(newProduct.getDescription());
            if(newProduct.getDeliveryType() == 0) {
                deliveryTypeDeliveryToggle.setSelected(true);
            }else if (newProduct.getDeliveryType() == 1){
                deliveryTypePickupToggle.setSelected(true);
            }
        });
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
        applyButton.setDisable(false);
        try {
            GetProductDto getProductDto = productService.getById(Integer.parseInt(params.get("productid"))).getData();
            productProperty.setValue(productMapper.mapGetProductDto(getProductDto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLeave() {

    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void onCancel() {
        router.back();
    }

    public void onApply() {
        try {
            Product oldProduct = productProperty.get();
            Product changedProduct = new Product();
            changedProduct.setProductId(oldProduct.getProductId());
            changedProduct.setOffername(nameField.getText());
            changedProduct.setSeller(oldProduct.getSeller());
            changedProduct.setPrice(Double.parseDouble(priceField.getText()));
            changedProduct.setCategory(categoryField.getText());
            changedProduct.setDescription(descriptionField.getText());
            changedProduct.setImg(oldProduct.getImg());
            changedProduct.setDeliveryType(oldProduct.getDeliveryType());
            productService.updateProduct(changedProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDelete() {
        try {
            ResponseEntity response = productService.deleteProduct(productProperty.get().getProductId());
            if(response.getStatus() == HttpStatus.OK_200){
                applyButton.setDisable(true);
            }else {
                System.err.println("Something went wrong!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
