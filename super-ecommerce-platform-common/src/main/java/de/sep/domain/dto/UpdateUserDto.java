package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    String username;
    String email;
    String street;
    String postcode;
    String city;
    String imageb64;
    int wantsTFA;
}
