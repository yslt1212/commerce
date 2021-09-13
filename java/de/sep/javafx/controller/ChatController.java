package de.sep.javafx.controller;

import de.sep.domain.dto.ConversationDto;
import de.sep.domain.dto.MessageDto;
import de.sep.domain.mapper.MessageMapper;
import de.sep.domain.mapper.UserMapper;
import de.sep.domain.model.Conversation;
import de.sep.domain.model.Message;
import de.sep.domain.model.User;
import de.sep.javafx.component.MessageCard;
import de.sep.javafx.routing.Routable;
import de.sep.javafx.routing.Router;
import de.sep.javafx.services.ImageService;
import de.sep.javafx.services.MessagingService;
import de.sep.javafx.services.UserService;
import de.sep.javafx.util.ResponseEntity;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.eclipse.jetty.http.HttpStatus.OK_200;

public class ChatController implements Routable {

    @FXML
    Button backButton;

    @FXML
    Circle userAvatar;

    @FXML
    Label usernameLabel;

    @FXML
    VBox messageContainer;

    @FXML
    ScrollPane messageScrollPane;

    @FXML
    TextField messageTextField;

    @FXML
    Button sendMessageButton;

    MessagingService messagingService;
    UserService userService;

    User user;

    MessageMapper messageMapper;
    UserMapper userMapper;
    ImageService imageService;

    Router router;

    Conversation conversation;

    public ChatController(){
        messageMapper = new MessageMapper();
        userMapper = new UserMapper();
    }

    public void initialize(){
        backButton.setOnAction(event -> router.back());
        messageContainer.heightProperty().addListener((observable, oldValue, newValue) -> messageScrollPane.setVvalue(1D));
        sendMessageButton.setOnAction(event -> {
            if(messageTextField.getText().length() != 0) {
                String messageText = messageTextField.getText();
                messageTextField.clear();
                MessageDto messageDto = new MessageDto();
                messageDto.setConversationId(conversation.getConversationId());
                messageDto.setSenderId(user.getUserId());
                messageDto.setTimestamp(System.currentTimeMillis());
                messageDto.setMessage(messageText);
                MessageCard messageCard = addMessageCard(new Message(0, messageDto.getSenderId(), messageDto.getMessage(), messageDto.getTimestamp()));
                try {
                    ResponseEntity response = messagingService.sendMessage(messageDto);
                    if (response.getStatus() != OK_200) {
                        messageContainer.getChildren().remove(messageCard.getParent());
                        messageTextField.setText(messageText);
                    }
                } catch (Exception e) {
                    messageContainer.getChildren().remove(messageCard.getParent());
                    messageTextField.setText(messageText);
                }
            }
        });
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        try {
            this.userService.getUser().addListener((observable, oldValue, newValue) -> {
                this.user = newValue;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }

    public MessageCard addMessageCard(Message message){
        boolean ownMessage = message.getSenderId() == this.user.getUserId();
        String author = message.getSenderId() == conversation.getInitiator().getUserId() ? conversation.getInitiator().getUsername() : conversation.getParticipant().getUsername();
        MessageCard messageCard = new MessageCard(message.getMessage(),author,message.getTimestamp());
        HBox wrapper = new HBox(messageCard);
        wrapper.setAlignment(ownMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageContainer.getChildren().add(wrapper);
        return messageCard;
    }

    private void fetchMessages() throws Exception {
        ResponseEntity<List<MessageDto>> response = messagingService.getMessages(conversation.getConversationId());
        messageContainer.getChildren().clear();
        if(response.getStatus() == OK_200){
            List<Message> messages = response.getData().parallelStream().map(messageMapper::mapMessageDto).collect(Collectors.toList());
            for(Message message : messages){
                addMessageCard(message);
            }
        }
    }

    private void setHeader(){
        User partnerUser;
        if(conversation.getInitiator().getUserId() == this.user.getUserId()){
            partnerUser = conversation.getParticipant();
        }else{
            partnerUser = conversation.getInitiator();
        }
        this.usernameLabel.setText(partnerUser.getUsername());
        try {
            this.userAvatar.setFill(new ImagePattern(imageService.base64StringToImage(partnerUser.getImg())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRouteWithParams(Map<String, String> params) {
        ResponseEntity<ConversationDto> response;
        ConversationDto conversationDto;
        try {
            if (params.containsKey("conversationId")) {
                int conversationId = Integer.parseInt(params.get("conversationId"));
                response = messagingService.getConversation(conversationId);
            } else if (params.containsKey("partnerId")) {
                int partnerId = Integer.parseInt(params.get("partnerId"));
                response = messagingService.startConversation(partnerId);
            }else{
                throw new IllegalArgumentException("needs either partnerId or conversationId");
            }

            if(response.getStatus() == OK_200){
                conversationDto = response.getData();
                Conversation conversation = new Conversation();
                conversation.setConversationId(conversationDto.getConversationId());
                conversation.setInitiator(userMapper.mapUserDto(conversationDto.getInitiator()));
                conversation.setParticipant(userMapper.mapUserDto(conversationDto.getParticipant()));
                conversation.setLastMessage(messageMapper.mapMessageDto(conversationDto.getLastMessage()));
                this.conversation = conversation;
                fetchMessages();
                setHeader();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLeave() {

    }


}
