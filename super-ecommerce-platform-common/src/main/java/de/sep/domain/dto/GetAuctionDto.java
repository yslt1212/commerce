package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAuctionDto {
    int auctionId;
    String offername;
    int seller;
    String category;
    String description;
    Timestamp endDate;
    double startPrice;
    double bidAmount;
    int deliveryType;
}

