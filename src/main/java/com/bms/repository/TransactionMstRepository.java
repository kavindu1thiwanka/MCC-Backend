package com.bms.repository;

import com.bms.entity.TransactionMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

import static com.bms.util.CommonConstants.STATUS_COMPLETE;

public interface TransactionMstRepository extends JpaRepository<TransactionMst, Integer> {

    @Query("SELECT trx FROM TransactionMst trx INNER JOIN ReservationMst res ON trx.reservationId = res.id " +
            "WHERE res.driverId = :driverId AND res.status = '" + STATUS_COMPLETE + "'")
    List<TransactionMst> getDriverEarningsByDriverId(@Param("driverId") Integer driverId);

    @Query("SELECT trx FROM TransactionMst trx WHERE trx.status = '" + STATUS_COMPLETE + "' ORDER BY trx.paymentDate ASC")
    List<TransactionMst> getTransactionDetails();

    @Query("SELECT trx FROM TransactionMst trx WHERE trx.status = '" + STATUS_COMPLETE + "' " +
            "AND trx.paymentDate BETWEEN :startDate AND :endDate ORDER BY trx.paymentDate ASC")
    List<TransactionMst> getTransactionDetailsByDate(Date startDate, Date endDate);
}
