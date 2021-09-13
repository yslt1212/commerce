package de.sep.domain.mapper;

import de.sep.domain.dto.MessageDto;
import de.sep.domain.entity.MessageEntity;
import de.sep.domain.model.Message;

public class MessageMapper {

    public MessageDto mapMessageDto(MessageEntity messageEntity){
        if(messageEntity == null)return null;
        MessageDto messageDto = new MessageDto();
        messageDto.setMessageId(messageEntity.getMessageId());
        messageDto.setConversationId(messageEntity.getConversationId());
        messageDto.setSenderId(messageEntity.getSenderId());
        messageDto.setMessage(messageEntity.getMessage());
        messageDto.setTimestamp(messageEntity.getTimestamp().getTime());
        return messageDto;
    }

    public Message mapMessageDto(MessageDto messageDto){
        if(messageDto == null)return null;
        Message message = new Message();
        message.setMessageId(messageDto.getMessageId());
        message.setMessage(messageDto.getMessage());
        message.setTimestamp(messageDto.getTimestamp());
        message.setSenderId(messageDto.getSenderId());
        return message;
    }

}
