package com.bms.controller;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.VehicleMstDto;
import com.bms.service.VehicleManagementService;
import com.bms.util.BMSCheckedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestScope
@RequestMapping(VEHICLE)
public class VehicleManagementController {

    private VehicleManagementService vehicleManagementService;

    @PostMapping(GET_VEHICLE_LIST_V1)
    public ResponseEntity<Object> getVehicleList(@RequestBody CommonFilterDto commonFilterDto) {
        return vehicleManagementService.getVehicleList(commonFilterDto);
    }

    @PostMapping(GET_VEHICLE_TOTAL_COST)
    public ResponseEntity<Object> getVehicleTotalCost(@RequestBody ReservationDto reservationDto) throws BMSCheckedException, StripeException {
        return vehicleManagementService.getVehicleTotalCost(reservationDto);
    }

    @PostMapping(value = ADD_VEHICLE_V1, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> addVehicle(
            @RequestPart("vehicleMstDto") String vehicleMstDtoJson,
            @RequestPart("vehicleImage") MultipartFile vehicleImage) throws BMSCheckedException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        VehicleMstDto vehicleMstDto = objectMapper.readValue(vehicleMstDtoJson, VehicleMstDto.class);

        return vehicleManagementService.addVehicle(vehicleMstDto, vehicleImage);
    }


    @PutMapping(UPDATE_VEHICLE_V1)
    public ResponseEntity<Object> updateVehicle(@RequestBody VehicleMstDto vehicleMstDto, @ModelAttribute MultipartFile vehicleImage) throws BMSCheckedException, IOException {
        return vehicleManagementService.updateVehicle(vehicleMstDto, vehicleImage);
    }

    @PutMapping(UPDATE_VEHICLE_STATUS_V1)
    public ResponseEntity<Object> updateVehicleStatus(@RequestParam String vehicleNumber, @RequestParam Character status) throws BMSCheckedException {
        return vehicleManagementService.updateVehicleStatus(vehicleNumber, status);
    }

    @GetMapping(GET_ALL_VEHICLE_LIST_V1)
    public ResponseEntity<Object> getAllVehicleList() {
        return vehicleManagementService.getAllVehicleList();
    }

    @GetMapping(GET_VEHICLE_DETAILS_V1)
    public ResponseEntity<Object> getVehicleDetails(@RequestParam String vehicleNumber) throws BMSCheckedException {
        return vehicleManagementService.getVehicleDetails(vehicleNumber);
    }

    @Autowired
    public void setVehicleManagementService(VehicleManagementService vehicleManagementService) {
        this.vehicleManagementService = vehicleManagementService;
    }
}
