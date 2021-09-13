package de.sep.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateVoucherDto {

    int userid;
    String code;
}
