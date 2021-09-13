package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySellsDto {
    int orderId;
    Timestamp deliveryDate;
    int buyerId;
    String seller;
    String offername;
    double price;
    int deliveryType;
}
