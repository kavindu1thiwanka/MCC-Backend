package com.bms.controller;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.VehicleMstDto;
import com.bms.service.VehicleManagementService;
import com.bms.util.BMSCheckedException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

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

    @PostMapping
    public ResponseEntity<Object> addVehicle(@RequestBody VehicleMstDto vehicleMstDto) {
        return vehicleManagementService.addVehicle(vehicleMstDto);
    }

    @Autowired
    public void setVehicleManagementService(VehicleManagementService vehicleManagementService) {
        this.vehicleManagementService = vehicleManagementService;
    }
}
