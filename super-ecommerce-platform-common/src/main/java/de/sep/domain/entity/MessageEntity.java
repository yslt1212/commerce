package de.sep.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
    int messageId;
    int conversationId;
    int senderId;
    String message;
    Timestamp timestamp;
}
