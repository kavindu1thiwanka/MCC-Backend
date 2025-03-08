package com.bms.entity;

import com.bms.dto.ReservationDto;
import com.bms.entity.abst.CommonBaseEntity;
import com.bms.util.CommonConstants;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "reservation_mst")
@EqualsAndHashCode(callSuper = true)
public class ReservationMst extends CommonBaseEntity implements Serializable {

    @Column(name = "vehicle_no", nullable = false)
    private String vehicleNo;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "pick_up_date", nullable = false)
    private Date pickUpDate;

    @Column(name = "return_date", nullable = false)
    private Date returnDate;

    @Column(name = "pick_up_location", nullable = false)
    private String pickUpLocation;

    @Column(name = "return_location", nullable = false)
    private String returnLocation;

    @Column(name = "on_trip")
    private Boolean onTrip;

    @Column(name = "payment_status", nullable = false)
    private Character paymentStatus;

    @Column(name = "status", nullable = false)
    private Character status;

    public ReservationMst(ReservationDto reservationDto) {
        this.vehicleNo = reservationDto.getVehicleNo();
        this.pickUpDate = reservationDto.getPickUpDate();
        this.returnDate = reservationDto.getReturnDate();
        this.pickUpLocation = reservationDto.getPickUpLocation();
        this.returnLocation = reservationDto.getReturnLocation() == null || reservationDto.getReturnLocation().isEmpty()
                ? reservationDto.getPickUpLocation() : reservationDto.getReturnLocation();
        this.status = CommonConstants.STATUS_INACTIVE;
        this.paymentStatus = CommonConstants.STATUS_NOT_PAID;
        this.driverId = 0;
        this.onTrip = false;
    }
}
