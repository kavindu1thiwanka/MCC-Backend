package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class ReservationDto {

    private String vehicleNo;
    private Boolean needDriver = Boolean.FALSE;
    private Date pickUpDate;
    private Date returnDate;
    private String pickUpLocation;
    private String returnLocation;
    private BigDecimal amount;
}
