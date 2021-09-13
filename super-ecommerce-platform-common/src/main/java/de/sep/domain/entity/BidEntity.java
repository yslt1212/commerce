package de.sep.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidEntity {
    int id;
    int auctionId;
    int bidderId;
    double bidAmount;
}
