package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CommonFilterDto {

    private String sortBy;
    private Date pickUpDate;
    private Date returnDate;
    private List<String> filters;
}
