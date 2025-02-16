package com.bms.repository;

import com.bms.entity.ReservationMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationMstRepository extends JpaRepository<ReservationMst, Integer> {

    @Query("SELECT res.vehicleNo,res FROM ReservationMst res INNER JOIN VehicleMst veh ON res.vehicleNo = veh.vehicleNo " +
            "WHERE res.status = 'A' AND veh.category = :category AND veh.status='A' AND veh.availability='Y'")
    List<Object[]> getAlreadyBookedVehicles(@Param("category") String category);
}
