package com.bms.controller;

import com.bms.exception.BusinessException;
import com.bms.service.DriverManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestMapping(DRIVER)
@RequestScope
public class DriverManagementController {

    private DriverManagementService driverManagementService;

    @PutMapping(UPDATE_ONLINE_STATUS_V1)
    public ResponseEntity<Object> updateOnlineStatus(@RequestBody Boolean isOnline) throws BusinessException {
        return driverManagementService.updateOnlineStatus(isOnline);
    }

    @GetMapping(GET_ONLINE_STATUS_V1)
    public ResponseEntity<Object> getDriverDashboardDetails() throws BusinessException {
        return driverManagementService.getDriverDashboardDetails();
    }

    @GetMapping(GET_RIDES_HISTORY_V1)
    public ResponseEntity<Object> getDriverRideHistory() throws BusinessException {
        return driverManagementService.getDriverRideHistory();
    }

    @Autowired
    public void setDriverManagementService(DriverManagementService driverManagementService) {
        this.driverManagementService = driverManagementService;
    }
}
