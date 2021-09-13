package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDto {
    int conversationId;
    GetUserDto initiator;
    GetUserDto participant;
    MessageDto lastMessage;
}
