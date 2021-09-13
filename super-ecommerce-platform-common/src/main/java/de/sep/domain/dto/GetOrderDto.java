package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderDto {
    int orderId;
    Timestamp deliveryDate;
    String seller;
    String offername;
    double price;
    int deliveryType;
}
