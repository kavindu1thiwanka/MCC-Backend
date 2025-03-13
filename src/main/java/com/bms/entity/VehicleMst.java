package com.bms.entity;

import com.bms.util.CommonConstants;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "vehicle_mst")
public class VehicleMst implements Serializable {

    @Id
    @Column(name = "vehicle_no", nullable = false)
    private String vehicleNo;

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "seats", nullable = false)
    private Integer seats;

    @Column(name = "gear_type", nullable = false)
    private Character gearType;

    @Column(name = "vehicle_image", nullable = false)
    private String vehicleImage;

    @Column(name = "price", nullable = false)
    private BigDecimal pricePerDay;

    @Column(name = "status", nullable = false)
    private Character status;

    @Column(name = "created_by")
    protected String createdBy;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = CommonConstants.US_DATE_FORMATS_STRING)
    protected Date createdOn;

    @Column(name = "update_by")
    protected String updateBy;

    @Column(name = "update_on")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = CommonConstants.US_DATE_FORMATS_STRING)
    protected Date updateOn;

    @Column(name = "delete_by")
    protected String deleteBy;

    @Column(name = "delete_on")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = CommonConstants.US_DATE_FORMATS_STRING)
    protected Date deleteOn;
}

