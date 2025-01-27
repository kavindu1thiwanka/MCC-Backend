package com.bms.controller;

import com.bms.dto.UserDto;
import com.bms.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bms.controller.abst.Mappings.CREATE_USER_V1;
import static com.bms.controller.abst.Mappings.USER;

@RestController
@RequestMapping(USER)
public class UserManagementController {

    private UserManagementService userManagementService;

    @PostMapping(CREATE_USER_V1)
    public ResponseEntity<Object> createUser(@RequestBody UserDto user) {
        return userManagementService.createUser(user);
    }

    @Autowired
    public void setUserManagementService(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
}
