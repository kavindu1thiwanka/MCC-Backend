package com.bms.dto;

import com.bms.entity.ReservationMst;
import com.bms.entity.TransactionMst;
import com.bms.entity.UserMst;
import com.bms.entity.VehicleMst;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class ReservationDto {

    private Integer id;
    private String vehicleNo;
    private Boolean needDriver = Boolean.FALSE;
    private Integer userId;
    private Integer driverId;
    private Date pickUpDate;
    private Date returnDate;
    private String pickUpLocation;
    private String returnLocation;
    private BigDecimal totalCost;
    private BigDecimal pricePerDay;
    private Boolean onTrip = Boolean.FALSE;
    private Character paymentStatus;
    private Character status;
    private String vehicleModel;

    private UserMst customerDetails = null;
    private UserMst driverDetails = null;

    public ReservationDto(ReservationMst mst) {
        this.id = mst.getId();
        this.vehicleNo = mst.getVehicleNo();
        this.pickUpDate = mst.getPickUpDate();
        this.returnDate = mst.getReturnDate();
        this.pickUpLocation = mst.getPickUpLocation();
        this.returnLocation = mst.getReturnLocation();
        this.userId = mst.getUserId();
        this.needDriver = mst.getDriverId() != null && mst.getDriverId() != 0;
        this.driverId = mst.getDriverId();
        this.onTrip = mst.getOnTrip();
        this.paymentStatus = mst.getPaymentStatus();
        this.status = mst.getStatus();
    }

    public ReservationDto(ReservationMst mst, UserMst customer, VehicleMst vehicle, BigDecimal amount) {
        this.id = mst.getId();
        this.vehicleNo = vehicle.getVehicleNo();
        this.vehicleModel = vehicle.getVehicleModel();
        this.pickUpDate = mst.getPickUpDate();
        this.returnDate = mst.getReturnDate();
        this.pickUpLocation = mst.getPickUpLocation();
        this.returnLocation = mst.getReturnLocation();
        this.userId = mst.getUserId();
        this.needDriver = mst.getDriverId() != null && mst.getDriverId() != 0;
        this.totalCost = amount;
        this.customerDetails = customer;
    }
}
