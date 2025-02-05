package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilterDto {

    private String value;
    private String operator;
}
