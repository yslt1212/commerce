package de.sep.javafx.controller;

import de.sep.domain.dto.ConversationDto;
import de.sep.domain.mapper.MessageMapper;
import de.sep.domain.mapper.UserMapper;
import de.sep.domain.model.Conversation;
import de.sep.javafx.component.ConversationCard;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ImageService;
import de.sep.javafx.services.MessagingService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConversationsController implements Routable {

    Router router;
    UserService userService;
    MessagingService messagingService;
    ImageService imageService;

    UserMapper userMapper;
    MessageMapper messageMapper;

    @FXML
    VBox conversationContainer;

    SimpleListProperty<Conversation> conversations = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    public ConversationsController(){
        userMapper = new UserMapper();
        messageMapper = new MessageMapper();
        conversations.addListener((observable, oldValue, newValue) -> {
            List<Conversation> conversations = newValue.stream().sorted((o1, o2) -> Long.signum((o1.getLastMessage() != null? o1.getLastMessage().getTimestamp():0) - (o2.getLastMessage() != null? o2.getLastMessage().getTimestamp():0))).collect(Collectors.toList());
            conversationContainer.getChildren().clear();
            for(Conversation conversation : conversations){
                ConversationCard conversationCard = new ConversationCard(conversation,router,userService,messagingService,imageService);
                conversationContainer.getChildren().add(conversationCard);
            }
            conversationContainer.layout();
        });
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void onRoute() {
        try {
            ResponseEntity<List<ConversationDto>> response = messagingService.getConversations();
            if(response.getStatus() == HttpStatus.OK_200){
                List<Conversation> conversations = response.getData()
                        .stream()
                        .map(conversationDto -> new Conversation(
                                conversationDto.getConversationId(),
                                userMapper.mapUserDto(conversationDto.getInitiator()),
                                userMapper.mapUserDto(conversationDto.getParticipant()),
                                messageMapper.mapMessageDto(conversationDto.getLastMessage())
                        )).collect(Collectors.toList());
                this.conversations.setAll(conversations);
            }
        }catch (NumberFormatException e){

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {

    }

    @Override
    public void onLeave() {

    }
}
