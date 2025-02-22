package com.bms.repository;

import com.bms.entity.ReservationMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationMstRepository extends JpaRepository<ReservationMst, Integer> {

    @Query("SELECT res.vehicleNo,res FROM ReservationMst res INNER JOIN VehicleMst veh ON res.vehicleNo = veh.vehicleNo " +
            "WHERE res.status = 'A' AND veh.category = :category AND veh.status='A' AND veh.availability='Y'")
    List<Object[]> getAlreadyBookedVehicles(@Param("category") String category);

    @Query("SELECT res.driverId FROM ReservationMst res WHERE res.status = 'A' " +
            "AND (:pickUpDate NOT BETWEEN res.pickUpDate AND res.returnDate) AND (:returnDate NOT BETWEEN res.pickUpDate AND res.returnDate)")
    List<Integer> getReservationUnavailableDrivers(Date pickUpDate, Date returnDate);

    @Query("SELECT res FROM ReservationMst res WHERE res.createdBy = :username ORDER BY res.id DESC")
    List<ReservationMst> getReservationDetailsByCreatedUser(@Param("username") String username);
}
