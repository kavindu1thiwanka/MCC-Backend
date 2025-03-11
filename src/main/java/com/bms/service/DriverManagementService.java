package com.bms.service;

import com.bms.exception.BusinessException;
import org.springframework.http.ResponseEntity;

public interface DriverManagementService {

    ResponseEntity<Object> updateOnlineStatus(Boolean isOnline) throws BusinessException;

    ResponseEntity<Object> getDriverDashboardDetails();

    ResponseEntity<Object> getDriverRideHistory();
}
