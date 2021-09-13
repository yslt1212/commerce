package de.sep.javafx.controller;

import de.sep.domain.dto.GetVoucherDto;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.net.URL;
import java.util.*;

public class VoucherController implements Routable, Initializable {


    @FXML
    ListView voucherList = new ListView();

    @FXML
    Label infoLabel;



    UserService userService;
    Router router;


    public VoucherController(){


    }
    
    @Override
    public void setRouter(Router router) {

    }

    @Override
    public void onRoute() {
        try {
            showVoucher();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

        try {
            showVoucher();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLeave() {
        infoLabel.setText("");

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        voucherList.setOnMouseClicked(mouseEvent -> {
            String s = voucherList.getSelectionModel().getSelectedItem().toString();
            String code = s.substring(5, 17);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(code);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
            infoLabel.setText("Code Copied to clipboard");
        });

    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private void showVoucher() throws Exception {
        try {

            List<String> list = new ArrayList<>();
            ResponseEntity<List<GetVoucherDto>> code = userService.showVoucher(userService.getUser().get().getUserId());

            for (GetVoucherDto gvdto: code.getData()) {
                list.add(String.format("code:%s, value:%d", gvdto.getCode(), gvdto.getValue()));

            }
            voucherList.getItems().setAll(list);



        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("List was empty:" + e);
        }

    }
}
