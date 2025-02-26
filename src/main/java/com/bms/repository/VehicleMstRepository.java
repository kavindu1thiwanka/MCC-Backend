package com.bms.repository;

import com.bms.entity.VehicleMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import static com.bms.util.CommonConstants.STATUS_DELETE;

public interface VehicleMstRepository extends JpaRepository<VehicleMst, String> {

    @Query("SELECT CASE WHEN COUNT(mst) > 0 THEN true ELSE false END FROM VehicleMst mst " +
            "WHERE mst.vehicleNo = :vehicleNo AND mst.status <> '" + STATUS_DELETE + "'")
    boolean existsByVehicleNo(@Param("vehicleNo") String vehicleNo);
}
