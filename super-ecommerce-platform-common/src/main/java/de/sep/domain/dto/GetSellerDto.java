package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSellerDto {
        int userId;
        String username;
        String email;
        String companyName;
}
