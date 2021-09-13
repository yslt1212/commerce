package de.sep.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class VoucherEntity {

    int code;
    int id;
    int value;
    int active;
    int userId;
}
