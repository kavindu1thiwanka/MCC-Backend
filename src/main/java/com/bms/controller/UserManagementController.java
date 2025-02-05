package com.bms.controller;

import com.bms.dto.UserDto;
import com.bms.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestMapping(USER)
public class UserManagementController {

    private UserManagementService userManagementService;

    @PostMapping(CREATE_USER_V1)
    public ResponseEntity<Object> createUser(@RequestBody UserDto user) {
        return userManagementService.createUser(user);
    }

    @PutMapping(UPDATE_USER_V1)
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user) {
        return userManagementService.updateUser(user);
    }

    @PutMapping(ACTIVATE_USER_V1)
    public ResponseEntity<Object> activateUser(@RequestParam Integer userId) {
        return userManagementService.activateUser(userId);
    }

    @PutMapping(INACTIVATE_USER_V1)
    public ResponseEntity<Object> inactivateUser(@RequestParam Integer userId) {
        return userManagementService.inactivateUser(userId);
    }

    @DeleteMapping(DELETE_USER_V1)
    public ResponseEntity<Object> deleteUser(@RequestParam Integer userId) {
        return userManagementService.deleteUser(userId);
    }

    @GetMapping(GET_USER_DETAILS_V1)
    public ResponseEntity<Object> getUserDetails(@RequestParam Integer userId) {
        return userManagementService.getUserDetails(userId);
    }

    @Autowired
    public void setUserManagementService(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
}
