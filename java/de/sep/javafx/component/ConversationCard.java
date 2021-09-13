package de.sep.javafx.component;

import de.sep.domain.model.Conversation;
import de.sep.domain.model.User;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ImageService;
import de.sep.javafx.services.MessagingService;
import de.sep.javafx.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.util.Map;

public class ConversationCard extends AnchorPane {

    @FXML
    Circle userAvatar;

    @FXML
    Label usernameLabel;

    @FXML
    Label lastMessageTextLabel;

    Router router;

    UserService userService;

    MessagingService messagingService;

    ImageService imageService;

    public ConversationCard(Conversation conversation, Router router, UserService userService, MessagingService messagingService, ImageService imageService){
        this.router = router;
        this.userService = userService;
        this.messagingService = messagingService;
        this.imageService = imageService;
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/component/conversationCard.fxml"));
        fxml.setRoot(this);
        fxml.setController(this);
        try {
            fxml.load();
            int loggedInUserId = userService.getUser().get().getUserId();
            User conversationee = loggedInUserId == conversation.getInitiator().getUserId() ? conversation.getParticipant() : conversation.getInitiator();
            if(conversationee != null){
                userAvatar.setFill(new ImagePattern(imageService.base64StringToImage(conversationee.getImg())));
                usernameLabel.setText(conversationee.getUsername());
            }
            if(conversation.getLastMessage() != null){
                lastMessageTextLabel.setText(conversation.getLastMessage().getMessage());
            }
            setOnMouseClicked(event -> router.route("/chat", Map.of("conversationId",String.valueOf(conversation.getConversationId()))));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
