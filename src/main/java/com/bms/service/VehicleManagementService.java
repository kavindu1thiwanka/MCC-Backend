package com.bms.service;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.VehicleMstDto;
import com.bms.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VehicleManagementService {

    ResponseEntity<Object> getVehicleList(CommonFilterDto commonFilterDto);

    ResponseEntity<Object> addVehicle(VehicleMstDto vehicleMstDto, MultipartFile vehicleImage) throws BusinessException, IOException;

    ResponseEntity<Object> getVehicleTotalCost(ReservationDto reservationDto);

    ResponseEntity<Object> updateVehicle(VehicleMstDto vehicleMstDto, MultipartFile vehicleImage) throws BusinessException, IOException;

    ResponseEntity<Object> updateVehicleStatus(String vehicleNumber, Character status) throws BusinessException;

    ResponseEntity<Object> getAllVehicleList();

    ResponseEntity<Object> getVehicleDetails(String vehicleNumber) throws BusinessException;
}
