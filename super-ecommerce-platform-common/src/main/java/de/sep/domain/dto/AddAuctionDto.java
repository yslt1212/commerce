package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAuctionDto {
    String offername;
    int seller;
    double startPrice;
    Timestamp endDate;
    String description;
    int deliveryType;
    String img;
}
