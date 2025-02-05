package com.bms.entity;

import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(name = "transaction_mst")
@EqualsAndHashCode(callSuper = true)
public class TransactionMst extends CommonBaseEntity implements Serializable {

    @Column(name = "reservation_id", nullable = false)
    private Integer reservationId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "payment_type", nullable = false)
    private Integer paymentType;

    @Column(name = "payment_date", nullable = false)
    private Date paymentDate;

    @Column(name = "status", nullable = false)
    private Character status;
}
