package com.bms.controller;

import com.bms.service.AdminManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import static com.bms.controller.abst.Mappings.ADMIN;
import static com.bms.controller.abst.Mappings.LOAD_ADMIN_DASHBOARD_DETAILS_V1;

@RestController
@RequestScope
@RequestMapping(ADMIN)
public class AdminManagementController {

    private AdminManagementService adminManagementService;

    @GetMapping(LOAD_ADMIN_DASHBOARD_DETAILS_V1)
    public ResponseEntity<Object> loadDashboardDetails() {
        return adminManagementService.loadDashboardDetails();
    }

    @Autowired
    public void setAdminManagementService(AdminManagementService adminManagementService) {
        this.adminManagementService = adminManagementService;
    }
}
