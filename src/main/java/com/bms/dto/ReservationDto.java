package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ReservationDto {

    private String vehicleNo;
    private Integer userId;
    private Boolean needDriver = Boolean.FALSE;
    private Integer driverId;
    private Date pickUpDate;
    private Date returnDate;
    private String pickUpLocation;
    private String returnLocation;
}
