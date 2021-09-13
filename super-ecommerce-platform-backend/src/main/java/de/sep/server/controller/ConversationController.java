package de.sep.server.controller;

import de.sep.domain.dto.ConversationDto;
import de.sep.domain.entity.ConversationEntity;
import de.sep.domain.mapper.MessageMapper;
import de.sep.domain.mapper.UserMapper;
import de.sep.server.adapter.MessageAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

public class ConversationController extends BaseController{

    public static final String PATH = "/user/conversation";

    MessageAdapter messageAdapter;
    UserAdapter userAdapter;
    MessageMapper messageMapper;
    UserMapper userMapper;

    public ConversationController() throws SQLException {
        messageAdapter = new MessageAdapter();
        userAdapter = new UserAdapter();
        messageMapper = new MessageMapper();
        userMapper = new UserMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
        ResponseEntity responseEntity = new ResponseEntity();
        try{
            authenticate(req.getSession());
            int initiatorId = (int)req.getSession().getAttribute("userid");
            if(req.getParameter("partnerId") != null) {
                int participantId = Integer.parseInt(req.getParameter("partnerId"));
                ConversationEntity conversationEntity = messageAdapter.createConversation(initiatorId,participantId);
                ConversationDto conversationDto = new ConversationDto();
                conversationDto.setConversationId(conversationEntity.getConversationId());
                conversationDto.setInitiator(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getInitiatorId())));
                conversationDto.setParticipant(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getParticipantId())));
                responseEntity.setData(conversationDto);
                responseEntity.setStatus(SC_OK);
            }
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        } catch (InternalServerErrorException | SQLException e) {
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage("An error occurred!");
            e.printStackTrace();
        } catch (NotFoundException e) {
            responseEntity.setStatus(SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        }
        try {
            sendResponse(resp,responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            authenticate(req.getSession());
            int userId = (int)req.getSession().getAttribute("userid");
            if(req.getParameter("partnerId") != null){
                int partnerId = Integer.parseInt(req.getParameter("partnerId"));
                ConversationEntity conversationEntity = messageAdapter.getConversationFromUsers(userId,partnerId);
                ConversationDto conversationDto = new ConversationDto();
                conversationDto.setConversationId(conversationEntity.getConversationId());
                conversationDto.setInitiator(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getInitiatorId())));
                conversationDto.setParticipant(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getParticipantId())));
                conversationDto.setLastMessage(messageMapper.mapMessageDto(messageAdapter.getLastMessage(conversationEntity.getConversationId())));
                responseEntity.setData(conversationDto);
                responseEntity.setStatus(SC_OK);
            }else if(req.getParameter("conversationId") != null){
                int conversationId = Integer.parseInt(req.getParameter("conversationId"));
                ConversationEntity conversationEntity = messageAdapter.getConversationById(conversationId);
                ConversationDto conversationDto = new ConversationDto();
                conversationDto.setConversationId(conversationEntity.getConversationId());
                conversationDto.setInitiator(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getInitiatorId())));
                conversationDto.setParticipant(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getParticipantId())));
                conversationDto.setLastMessage(messageMapper.mapMessageDto(messageAdapter.getLastMessage(conversationEntity.getConversationId())));
                responseEntity.setData(conversationDto);
                responseEntity.setStatus(SC_OK);
            }else {
                List<ConversationDto> conversations = messageAdapter.getConversationsFromUser(userId).stream().map(conversationEntity -> {
                    try {
                        ConversationDto conversationDto = new ConversationDto();
                        conversationDto.setConversationId(conversationEntity.getConversationId());
                        conversationDto.setInitiator(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getInitiatorId())));
                        conversationDto.setParticipant(userMapper.mapUserDto(userAdapter.getUserById(conversationEntity.getParticipantId())));
                        conversationDto.setLastMessage(messageMapper.mapMessageDto(messageAdapter.getLastMessage(conversationEntity.getConversationId())));
                        return conversationDto;
                    } catch (InternalServerErrorException | SQLException e) {
                        e.printStackTrace();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
                responseEntity.setData(conversations);
                responseEntity.setStatus(SC_OK);
            }
        } catch (UnauthorizedException e) {
            responseEntity.setStatus(SC_UNAUTHORIZED);
            responseEntity.setMessage(e.getMessage());
        }catch (SQLException | InternalServerErrorException e){
            responseEntity.setStatus(SC_INTERNAL_SERVER_ERROR);
            responseEntity.setMessage("An error occurred!");
            e.printStackTrace();
        } catch (NotFoundException e) {
            responseEntity.setStatus(SC_NOT_FOUND);
            responseEntity.setMessage(e.getMessage());
        }
        try {
            sendResponse(resp,responseEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
