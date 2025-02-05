package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class CommonFilterDto {

    private String sortFiled;
    private String sortOrder;
    private Map<String, FilterDto> filters;
}
