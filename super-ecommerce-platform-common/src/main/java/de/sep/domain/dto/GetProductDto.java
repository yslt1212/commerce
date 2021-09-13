package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class GetProductDto {
    int offerId;
    int productId;
    String offername;
    int seller;
    double price;
    String category;
    String description;
    int deliveryType;
}
