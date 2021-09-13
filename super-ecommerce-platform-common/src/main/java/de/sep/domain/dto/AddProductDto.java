package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductDto {
    String productname;
    int seller;
    double price;
    String category;
    String description;
    String img;
    int deliveryType;
}
