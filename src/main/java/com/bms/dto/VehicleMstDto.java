package com.bms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleMstDto {

    @JsonProperty("vehicleNo")
    private String vehicleNo;
    @JsonProperty("model")
    private String name;
    @JsonProperty("vehicleType")
    private String vehicleType;
    @JsonProperty("seats")
    private Integer seats;
    @JsonProperty("gearType")
    private String gearType;
    @JsonProperty("category")
    private String category;
    @JsonProperty("pricePerDay")
    private BigDecimal pricePerDay;
}
