package de.sep.javafx.services;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.sep.domain.dto.ConversationDto;
import de.sep.domain.dto.MessageDto;
import de.sep.domain.mapper.ProductMapper;
import de.sep.javafx.util.ResponseEntity;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;

import java.lang.reflect.Type;
import java.util.List;

public class MessagingService {

    HttpClient client;
    GsonBuilder gsonBuilder = new GsonBuilder();
    ProductMapper productMapper;

    public MessagingService(HttpClient client) {
        this.client = client;
        productMapper = new ProductMapper();
    }

    public ResponseEntity<MessageDto> sendMessage(MessageDto messageDto) throws Exception {
        client.start();
        Request request = client.POST("http://localhost:8080/user/message");

        request.header(HttpHeader.CONTENT_TYPE, "application/json");
        request.content(new StringContentProvider(gsonBuilder.create().toJson(messageDto)));

        ContentResponse response = request.send();

        Type responseEntityType = new TypeToken<ResponseEntity<MessageDto>>() {}.getType();
        ResponseEntity<MessageDto> responseEntity = gsonBuilder.create().fromJson(response.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<ConversationDto> startConversation(int partnerId) throws Exception {
        client.start();
        Request request = client.POST(String.format("http://localhost:8080/user/conversation?partnerId=%d",partnerId));

        ContentResponse response = request.send();
        Type responseEntityType = new TypeToken<ResponseEntity<ConversationDto>>() {}.getType();
        ResponseEntity<ConversationDto> responseEntity = gsonBuilder.create().fromJson(response.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<List<ConversationDto>> getConversations() throws Exception {
        client.start();
        ContentResponse res = client.GET("http://localhost:8080/user/conversation");

        Type responseEntityType = new TypeToken<ResponseEntity<List<ConversationDto>>>() {}.getType();
        ResponseEntity<List<ConversationDto>> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<ConversationDto> getConversation(int conversationId) throws Exception {
        client.start();
        ContentResponse res = client.GET(String.format("http://localhost:8080/user/conversation?conversationId=%d",conversationId));

        Type responseEntityType = new TypeToken<ResponseEntity<ConversationDto>>() {}.getType();
        ResponseEntity<ConversationDto> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<ConversationDto> getConversationWithPartner(int partnerId) throws Exception {
        client.start();
        ContentResponse res = client.GET(String.format("http://localhost:8080/user/conversation?partner=%d",partnerId));

        Type responseEntityType = new TypeToken<ResponseEntity<ConversationDto>>() {}.getType();
        ResponseEntity<ConversationDto> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

    public ResponseEntity<List<MessageDto>> getMessages(int conversationId) throws Exception {
        client.start();
        ContentResponse res = client.GET(String.format("http://localhost:8080/user/message?conversationId=%d",conversationId));

        Type responseEntityType = new TypeToken<ResponseEntity<List<MessageDto>>>() {}.getType();
        ResponseEntity<List<MessageDto>> responseEntity = gsonBuilder.create().fromJson(res.getContentAsString(), responseEntityType);
        client.stop();
        return responseEntity;
    }

}
