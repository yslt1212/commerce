package de.sep.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingEntity {
    int id;
    int authorId;
    int ratedUserId;
    int rating;
    String comment;
}
