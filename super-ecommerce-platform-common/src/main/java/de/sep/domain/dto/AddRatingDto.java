package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class AddRatingDto {
    int userId;
    int ratedUserId;
    int rating;
    String comment;
}
