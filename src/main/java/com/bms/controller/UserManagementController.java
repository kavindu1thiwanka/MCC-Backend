package com.bms.controller;

import com.bms.dto.AddressDto;
import com.bms.dto.UserDto;
import com.bms.service.UserManagementService;
import com.bms.util.BMSCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestMapping(USER)
public class UserManagementController {

    private UserManagementService userManagementService;

    @PostMapping(REGISTER_USER_V1)
    public ResponseEntity<Object> registerUser(@RequestBody UserDto user) throws BMSCheckedException {
        return userManagementService.registerUser(user);
    }

    @GetMapping(CONFIRM_USER_EMAIL_V1)
    public ResponseEntity<Object> confirmUserEmail(@RequestParam String uuid) throws BMSCheckedException {
        return userManagementService.confirmUserEmail(uuid);
    }

    @PutMapping(UPDATE_USER_V1)
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user) throws BMSCheckedException {
        return userManagementService.updateUser(user);
    }

    @PutMapping(ACTIVATE_USER_V1)
    public ResponseEntity<Object> activateUser(@RequestParam Integer userId) throws BMSCheckedException {
        return userManagementService.activateUser(userId);
    }

    @PutMapping(INACTIVATE_USER_V1)
    public ResponseEntity<Object> inactivateUser(@RequestParam Integer userId) throws BMSCheckedException {
        return userManagementService.inactivateUser(userId);
    }

    @DeleteMapping(DELETE_USER_V1)
    public ResponseEntity<Object> deleteUser(@RequestParam Integer userId) throws BMSCheckedException {
        return userManagementService.deleteUser(userId);
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
    public ResponseEntity<Object> updateUserAddress(@RequestBody AddressDto address) throws BMSCheckedException {
        return userManagementService.updateUserAddress(address);
    }

    @Autowired
    public void setUserManagementService(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
}
