package com.bms.repository;

import com.bms.entity.VehicleMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static com.bms.util.CommonConstants.STATUS_DELETE;

public interface VehicleMstRepository extends JpaRepository<VehicleMst, String> {

    @Query("SELECT CASE WHEN COUNT(mst) > 0 THEN true ELSE false END FROM VehicleMst mst " +
            "WHERE mst.vehicleNo = :vehicleNo AND mst.status <> '" + STATUS_DELETE + "'")
    boolean existsByVehicleNo(@Param("vehicleNo") String vehicleNo);

    @Query("SELECT mst FROM VehicleMst mst WHERE mst.vehicleNo = :vehicleNo AND mst.status <> '" + STATUS_DELETE + "'")
    Optional<VehicleMst> findByVehicleNo(String vehicleNo);

    @Query("SELECT mst FROM VehicleMst mst WHERE mst.status <> '" + STATUS_DELETE + "' ORDER BY mst.category ASC, mst.createdOn DESC")
    List<VehicleMst> getVehicleListGroupByCategory();

    @Query("SELECT mst.vehicleModel FROM VehicleMst mst WHERE mst.vehicleNo = :vehicleNo")
    String getVehicleModelByVehicleNumber(String vehicleNo);
}
