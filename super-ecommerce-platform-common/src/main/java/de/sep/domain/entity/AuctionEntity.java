package de.sep.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionEntity {
    int id;
    String name;
    int seller;
    String category;
    String description;
    Timestamp endDate;
    double startPrice;
    double bidAmount;
}
