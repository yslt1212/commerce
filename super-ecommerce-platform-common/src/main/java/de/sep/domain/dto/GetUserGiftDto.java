package de.sep.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserGiftDto {
    int userid;
    String img;
    String username;

}
