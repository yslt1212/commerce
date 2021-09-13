package de.sep.server.adapter;

import de.sep.domain.entity.ConversationEntity;
import de.sep.domain.entity.MessageEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MessageAdapter extends DataAdapter{

    public MessageAdapter() throws SQLException {
        super("message");
    }

    public MessageEntity addMessage(MessageEntity messageEntity) throws SQLException {
        String query = String.format("INSERT INTO message (conversation_id, sender_id,message) VALUES('%d','%d','%s')",messageEntity.getConversationId(),messageEntity.getSenderId(),messageEntity.getMessage());
        messageEntity.setMessageId(addQuery(query));
        return messageEntity;
    }

    public List<ConversationEntity> getConversationsFromUser(int userId) throws SQLException {
        List<ConversationEntity> conversations = new LinkedList<>();
        String query = String.format("SELECT * FROM conversation WHERE initiator_id = '%d' OR participant_id = '%d'",userId,userId);
        ResultSet result = getQuery(query);
        if(result.next()){
            do{
                ConversationEntity conversation = new ConversationEntity(result);
                conversations.add(conversation);
            }while(result.next());
        }
        return conversations;
    }

    public ConversationEntity createConversation(int initiator_id, int participant_id) throws SQLException {
        ConversationEntity conversationEntity = new ConversationEntity();
        String query = String.format("INSERT INTO conversation (initiator_id, participant_id) VALUES (%d,%d)",initiator_id,participant_id);
        int conversation_id = addQuery(query);
        conversationEntity.setConversationId(conversation_id);
        conversationEntity.setInitiatorId(initiator_id);
        conversationEntity.setParticipantId(participant_id);
        return conversationEntity;
    }

    public ConversationEntity getConversationFromUsers(int userId, int partnerId) throws SQLException {
        ConversationEntity conversation = null;
        String query = String.format("SELECT * FROM conversation WHERE initiator_id = '%d' AND participant_id '%d' OR initiator_id = '%d' AND participant_id = '%d'",userId,partnerId,partnerId,userId);
        ResultSet result = getQuery(query);
        if(result.next()){
            conversation = new ConversationEntity(result);
        }
        return conversation;
    }

    public ConversationEntity getConversationById(int conversationId) throws SQLException {
        ConversationEntity conversation = null;
        String query = String.format("SELECT * FROM conversation WHERE conversation_id = '%d'",conversationId);
        ResultSet result = getQuery(query);
        if(result.next()){
            conversation = new ConversationEntity(result);
        }
        return conversation;
    }

    public MessageEntity getLastMessage(int conversationId) throws SQLException {
        MessageEntity messageEntity = null;
        String query = String.format("SELECT * FROM message WHERE conversation_id = '%d' ORDER BY timestamp DESC LIMIT 1",conversationId);
        ResultSet result = getQuery(query);
        if(result.next()){
            messageEntity = new MessageEntity();
            messageEntity.setMessageId(result.getInt("message_id"));
            messageEntity.setConversationId(result.getInt("conversation_id"));
            messageEntity.setSenderId(result.getInt("sender_id"));
            messageEntity.setMessage(result.getString("message"));
            messageEntity.setTimestamp(result.getTimestamp("timestamp"));
        }
        return messageEntity;
    }

    boolean conversationExists(int initiatorId, int participantId){
        // if conversation with id's exists return true
        // otherwise false
        return false;
    }

    public MessageEntity getMessage(int messageId){
        // get single message by id
        return null;
    }

    public List<MessageEntity> getMessages(int conversationId) throws SQLException {
        List<MessageEntity> messages = new LinkedList<>();
        String query = String.format("SELECT * FROM message WHERE conversation_id = '%d' ORDER BY timestamp ASC",conversationId);
        ResultSet result = getQuery(query);
        if(result.next()){
            do {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setMessageId(result.getInt("message_id"));
                messageEntity.setConversationId(result.getInt("conversation_id"));
                messageEntity.setSenderId(result.getInt("sender_id"));
                messageEntity.setMessage(result.getString("message"));
                messageEntity.setTimestamp(result.getTimestamp("timestamp"));
                messages.add(messageEntity);
            }while(result.next());
        }
        return messages;
    }

}
