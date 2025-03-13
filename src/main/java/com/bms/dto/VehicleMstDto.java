package com.bms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleMstDto {

    @JsonProperty("vehicleNo")
    private String vehicleNo;
    @JsonProperty("vehicleModel")
    private String name;
    @JsonProperty("vehicleType")
    private String vehicleType;
    @JsonProperty("seats")
    private Integer seats;
    @JsonProperty("gearType")
    private Character gearType;
    @JsonProperty("category")
    private String category;
    @JsonProperty("pricePerDay")
    private BigDecimal pricePerDay;
    @JsonProperty("vehicleImage")
    private String vehicleImage;

    public VehicleMstDto(String vehicleNo, String name, String vehicleType, Integer seats, Character gearType, String vehicleImage,
                         String category, BigDecimal pricePerDay) {
        this.vehicleNo = vehicleNo;
        this.name = name;
        this.vehicleType = vehicleType;
        this.seats = seats;
        this.gearType = gearType;
        this.category = category;
        this.vehicleImage = vehicleImage;
        this.pricePerDay = pricePerDay;
    }
}
