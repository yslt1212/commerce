package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    int messageId;
    int conversationId;
    int senderId;
    String message;
    long timestamp;
}
