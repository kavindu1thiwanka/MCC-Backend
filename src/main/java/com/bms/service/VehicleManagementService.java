package com.bms.service;

import com.bms.dto.CommonFilterDto;
import org.springframework.http.ResponseEntity;

public interface VehicleManagementService {

    ResponseEntity<Object> getVehicleList(CommonFilterDto commonFilterDto);
}
