package de.sep.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    int orderId;
    Timestamp deliveryDate;
    int buyerId;
    String seller;
    String offername;

}
