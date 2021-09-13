package de.sep.server.controller;

import com.google.gson.reflect.TypeToken;
import de.sep.domain.dto.AddProductDto;
import de.sep.domain.dto.MessageDto;
import de.sep.domain.entity.ConversationEntity;
import de.sep.domain.entity.MessageEntity;
import de.sep.domain.mapper.MessageMapper;
import de.sep.server.adapter.MessageAdapter;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

public class MessageController extends BaseController{

    public static final String PATH = "/user/message";

    MessageAdapter messageAdapter;
    MessageMapper messageMapper;

    public MessageController() throws SQLException {
        messageAdapter = new MessageAdapter();
        messageMapper = new MessageMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        ResponseEntity<List<MessageDto>> responseEntity = new ResponseEntity<>();
        try {
            authenticate(req.getSession());
            int conversationId = Integer.parseInt(req.getParameter("conversationId"));
            List<MessageDto> messages = messageAdapter.getMessages(conversationId)
                    .parallelStream()
                    .map(messageMapper::mapMessageDto)
                    .collect(Collectors.toList());
            responseEntity.setStatus(SC_OK);
            responseEntity.setData(messages);
        }catch (Exception e){
            responseEntity = getExceptionResponseEntity(e);
        }
        try {
            sendResponse(resp,responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp){
        ResponseEntity<MessageDto> responseEntity = new ResponseEntity<>();
        try{
            authenticate(request.getSession());
            Type requestBodyType = new TypeToken<MessageDto>() {}.getType();
            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            MessageDto sentMessage = gson.fromJson(requestBody,requestBodyType);
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setConversationId(sentMessage.getConversationId());
            messageEntity.setSenderId(sentMessage.getSenderId());
            messageEntity.setMessage(sentMessage.getMessage());
            messageEntity.setTimestamp(new Timestamp(sentMessage.getTimestamp()));
            messageEntity = messageAdapter.addMessage(messageEntity);
            responseEntity.setData(messageMapper.mapMessageDto(messageEntity));
            responseEntity.setStatus(SC_OK);
        } catch (Exception e){
            responseEntity = getExceptionResponseEntity(e);
        }
        try {
            sendResponse(resp,responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
