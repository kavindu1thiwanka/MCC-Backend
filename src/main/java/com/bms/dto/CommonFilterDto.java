package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CommonFilterDto {

    private String sortBy;
    private List<String> filters;
}
