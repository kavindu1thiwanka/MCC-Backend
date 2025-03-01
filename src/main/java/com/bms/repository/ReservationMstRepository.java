package com.bms.repository;

import com.bms.entity.ReservationMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

import static com.bms.util.CommonConstants.STATUS_FAILED;
import static com.bms.util.CommonConstants.STATUS_INACTIVE;

public interface ReservationMstRepository extends JpaRepository<ReservationMst, Integer> {

    @Query("SELECT res.vehicleNo,res FROM ReservationMst res INNER JOIN VehicleMst veh ON res.vehicleNo = veh.vehicleNo " +
            "WHERE res.status = 'A' AND veh.category = :category AND veh.status='A'")
    List<Object[]> getAlreadyBookedVehicles(@Param("category") String category);

    @Query("SELECT res.driverId FROM ReservationMst res WHERE res.status = 'A' " +
            "AND (:pickUpDate NOT BETWEEN res.pickUpDate AND res.returnDate) AND (:returnDate NOT BETWEEN res.pickUpDate AND res.returnDate)")
    List<Integer> getReservationUnavailableDrivers(Date pickUpDate, Date returnDate);

    @Query("SELECT res FROM ReservationMst res WHERE res.createdBy = :username " +
            "AND res.status NOT IN ('" + STATUS_FAILED + "' , '" + STATUS_INACTIVE + "') ORDER BY res.id DESC")
    List<ReservationMst> getReservationDetailsByCreatedUser(@Param("username") String username);

    @Query("SELECT res FROM ReservationMst res WHERE res.driverId = :id AND res.status = 'A' " +
            "AND res.pickUpDate >= :date ORDER BY res.pickUpDate ASC")
    List<ReservationMst> getUpcomingReservationsByDriverId(Date date, Integer id);
}
