package com.bms.service;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.VehicleMstDto;
import org.springframework.http.ResponseEntity;

public interface VehicleManagementService {

    ResponseEntity<Object> getVehicleList(CommonFilterDto commonFilterDto);

    ResponseEntity<Object> addVehicle(VehicleMstDto vehicleMstDto);

    ResponseEntity<Object> getVehicleTotalCost(ReservationDto reservationDto);
}
