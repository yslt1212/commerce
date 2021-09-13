package de.sep.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    int id;
    String name;
    int sellerId;
    double price;
    String category;
    String description;
    String img;
    int deliveryType;
}
