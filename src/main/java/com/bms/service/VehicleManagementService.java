package com.bms.service;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.VehicleMstDto;
import com.bms.util.BMSCheckedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VehicleManagementService {

    ResponseEntity<Object> getVehicleList(CommonFilterDto commonFilterDto);

    ResponseEntity<Object> addVehicle(VehicleMstDto vehicleMstDto, MultipartFile vehicleImage) throws BMSCheckedException, IOException;

    ResponseEntity<Object> getVehicleTotalCost(ReservationDto reservationDto);
}
