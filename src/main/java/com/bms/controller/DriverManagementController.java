package com.bms.controller;

import com.bms.service.DriverManagementService;
import com.bms.util.BMSCheckedException;
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
    public ResponseEntity<Object> updateOnlineStatus(@RequestBody Boolean isOnline) throws BMSCheckedException {
        return driverManagementService.updateOnlineStatus(isOnline);
    }

    @GetMapping(GET_ONLINE_STATUS_V1)
    public ResponseEntity<Object> getDriverDashboardDetails() throws BMSCheckedException {
        return driverManagementService.getDriverDashboardDetails();
    }

    @Autowired
    public void setDriverManagementService(DriverManagementService driverManagementService) {
        this.driverManagementService = driverManagementService;
    }
}
