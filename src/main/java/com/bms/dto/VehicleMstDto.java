package com.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleMstDto {

    private String vehicleNo;
    private String name;
    private String vehicleType;
    private Integer seats;
    private String gearType;
    private String vehicleImage;
}
