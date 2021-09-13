package de.sep.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    int id;
    String username;
    String password;
    String email;
    String street;
    String postcode;
    String city;
    double balance;
    int type;
    String img;
    int wantsTFA;
}
