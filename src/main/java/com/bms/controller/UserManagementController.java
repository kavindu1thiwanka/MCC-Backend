package com.bms.controller;

import com.bms.dto.AddressDto;
import com.bms.dto.UserDto;
import com.bms.exception.BusinessException;
import com.bms.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.util.Map;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestMapping(USER)
@RequestScope
public class UserManagementController {

    private UserManagementService userManagementService;

    @PostMapping(REGISTER_USER_V1)
    public ResponseEntity<Object> registerUser(@RequestBody UserDto user) throws BusinessException {
        return userManagementService.registerUser(user);
    }

    @PostMapping(CREATE_USER_V1)
    public ResponseEntity<Object> createUser(@ModelAttribute UserDto user) throws BusinessException, IOException {
        return userManagementService.createUser(user);
    }

    @GetMapping(CONFIRM_USER_EMAIL_V1)
    public ResponseEntity<Object> confirmUserEmail(@RequestParam String uuid) throws BusinessException {
        return userManagementService.confirmUserEmail(uuid);
    }

    @PutMapping(UPDATE_USER_V1)
    public ResponseEntity<Object> updateUser(@ModelAttribute UserDto user) throws BusinessException, IOException {
        return userManagementService.updateUser(user);
    }

    @PutMapping(CHANGE_USER_STATUS_V1)
    public ResponseEntity<Object> changeUserStatus(@RequestParam Integer userId, @RequestParam Character status) throws BusinessException {
        return userManagementService.changeUserStatus(userId, status);
    }

    @GetMapping(GET_USER_DETAILS_V1)
    public ResponseEntity<Object> getUserDetails(@RequestParam Integer userId) {
        return userManagementService.getUserDetails(userId);
    }

    @GetMapping(GET_USER_ADDRESS_V1)
    public ResponseEntity<Object> getUserAddress() {
        return userManagementService.getUserAddress();
    }

    @PutMapping(UPDATE_USER_ADDRESS_V1)
    public ResponseEntity<Object> updateUserAddress(@RequestBody AddressDto address) throws BusinessException {
        return userManagementService.updateUserAddress(address);
    }

    @PostMapping(RESET_PASSWORD_V1)
    public ResponseEntity<Object> resetPassword(@RequestBody Map<String, Object> requestBody) throws BusinessException {
        return userManagementService.resetPassword(requestBody);
    }

    @GetMapping(GET_LOGGED_IN_USER_DETAILS_V1)
    public ResponseEntity<Object> getLoggedInUserDetails() {
        return userManagementService.getLoggedInUserDetails();
    }

    @GetMapping(GET_ALL_USERS_V1)
    public ResponseEntity<Object> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @GetMapping(GET_ALL_DRIVERS_V1)
    public ResponseEntity<Object> getAllDrivers() {
        return userManagementService.getAllDrivers();
    }

    @GetMapping(GET_ALL_ADMIN_V1)
    public ResponseEntity<Object> getAllAdmins() {
        return userManagementService.getAllAdmins();
    }

    @Autowired
    public void setUserManagementService(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
}
