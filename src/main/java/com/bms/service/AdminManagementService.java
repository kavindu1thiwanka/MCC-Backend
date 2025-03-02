package com.bms.service;

import org.springframework.http.ResponseEntity;

public interface AdminManagementService {

    ResponseEntity<Object> loadDashboardDetails();
}
