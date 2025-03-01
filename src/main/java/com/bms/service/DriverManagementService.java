package com.bms.service;

import com.bms.util.BMSCheckedException;
import org.springframework.http.ResponseEntity;

public interface DriverManagementService {

    ResponseEntity<Object> updateOnlineStatus(Boolean isOnline) throws BMSCheckedException;

    ResponseEntity<Object> getOnlineStatus();
}
