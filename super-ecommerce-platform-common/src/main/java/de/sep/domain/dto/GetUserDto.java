package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserDto {
    int userid;
    String username;
    String email;
    String street;
    String postcode;
    String city;
    Double balance;
    int type;
    String img;
    int wantsTFA;

}
