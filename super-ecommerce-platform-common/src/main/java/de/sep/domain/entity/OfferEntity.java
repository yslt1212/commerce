package de.sep.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferEntity {
    int id;
    String name;
    int sellerId;
    String category;
    String description;
    String img;
    int deliveryType;
}

