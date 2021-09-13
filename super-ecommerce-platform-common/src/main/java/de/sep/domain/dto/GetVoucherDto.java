package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class GetVoucherDto {

    int voucher_id;
    String code;
    int value;
    int active;
    int user_id;


}
