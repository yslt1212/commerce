package de.sep.javafx.component.sidebar;

import de.sep.javafx.controller.HomeController;
import de.sep.javafx.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class Sidebar extends VBox {

    @FXML VBox sidebarContainer;
    @FXML
    Label header;
    String headerText;


    Button toggleBtn = new Button();

    Logger logger = new Logger(this.getClass().getCanonicalName());
    double prefWidthOut;
    double prefWidthIn;
    boolean toggled = false;
    HomeController homeController;
    List<SidebarButton> sidebarButtons;


    public Sidebar(Double prefWidthOut,
                   Double prefWidthIn,
                   boolean in,
                   List<SidebarButton> sidebarButtons,
                   String btnStyleClass,
                   String headerText
                                 ) {
        this.prefWidthOut = prefWidthOut;
        this.prefWidthIn = prefWidthIn;
        this.sidebarButtons = sidebarButtons;
        this.headerText = headerText;

        FXMLLoader sidebar = new FXMLLoader(getClass().getResource("/sidebar.fxml"));
        sidebar.setRoot(this);
        sidebar.setController(this);
        try {
            sidebar.load();
            for(SidebarButton sb: sidebarButtons) {
                sb.getButton().getStyleClass().add(btnStyleClass);
                sidebarContainer.getChildren().add(sb.getButton());
            }
            sidebarContainer.getChildren().add(toggleBtn);
            toggleBtn.getStyleClass().add(btnStyleClass);
            toggleBtn.setOnAction(actionEvent -> {
                if(this.toggled) {
                    toggleIn();
                } else {
                    toggleOut();
                }
            });

            if(in) {
                this.toggleIn();
            } else  {
                this.toggleOut();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void toggleOut() {
        sidebarContainer.setPrefWidth(prefWidthOut);
        header.setGraphic(null);
        header.setText(headerText);
        for(SidebarButton sd : sidebarButtons) {
            sd.getButton().setMaxWidth(prefWidthOut);
            sd.getButton().setGraphic(null);
            sd.getButton().setText(sd.getOutLabel());
        }
        toggleBtn.setText("<<<");
        toggleBtn.setPrefWidth(prefWidthOut);
        toggled = true;

    }

    private void toggleIn() {
        sidebarContainer.setPrefWidth(prefWidthIn);
        Image image = new Image("/img/dashboard.png");
        ImageView view = new ImageView(image);
        view.setFitHeight(40);
        view.setSmooth(true);
        view.setCache(true);
        view.setPreserveRatio(true);
        header.setGraphic(view);
        header.setAlignment(Pos.CENTER);
        header.setText("");

        toggleBtn.setText(">>>");
        toggleBtn.setPrefWidth(prefWidthIn);


        for(SidebarButton sd : sidebarButtons) {
            ImageView currView = new ImageView(sd.getInIcon());
            sd.getButton().setMaxWidth(prefWidthIn);

            currView.setFitHeight(40);
            currView.setSmooth(true);
            currView.setCache(true);
            currView.setPreserveRatio(true);

            sd.getButton().setGraphic(currView);
            sd.getButton().setAlignment(Pos.CENTER);
            sd.getButton().setText("");
        }


        toggled = false;
    }

    public void toggleSidebar() {
        if(!toggled) {
            toggleOut();
        } else  {
            toggleIn();
        }
    }
    
}
