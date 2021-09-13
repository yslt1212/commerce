package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {
    String username;
    String password;
    String email;
    String street;
    String postcode;
    String city;
    int balance;
    String img_src;
    String company;
    int type;
}
