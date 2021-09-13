package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDto {
    String name;
    String category;
    String description;
    String image;
    int deliveryType;
    BigDecimal price;
}

