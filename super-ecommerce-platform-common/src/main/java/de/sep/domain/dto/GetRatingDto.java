package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data

@NoArgsConstructor
public class GetRatingDto {
    int ratingId;
    int userId;
    int rating;
    String comment;
    String authorName;
}
