package de.sep.domain.entity;


import de.sep.domain.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationEntity {
    int conversationId;
    int initiatorId;
    int participantId;

    public ConversationEntity(ResultSet resultSet) throws SQLException {
        conversationId = resultSet.getInt("conversation_id");
        initiatorId = resultSet.getInt("initiator_id");
        participantId = resultSet.getInt("participant_id");
    }
}
