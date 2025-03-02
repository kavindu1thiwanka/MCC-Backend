package com.bms.service;

import com.bms.dto.AddressDto;
import com.bms.dto.UserDto;
import com.bms.util.BMSCheckedException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserManagementService {

    ResponseEntity<Object> registerUser(UserDto user) throws BMSCheckedException;

    ResponseEntity<Object> updateUser(UserDto user) throws BMSCheckedException;

    ResponseEntity<Object> activateUser(Integer userId) throws BMSCheckedException;

    ResponseEntity<Object> inactivateUser(Integer userId) throws BMSCheckedException;

    ResponseEntity<Object> deleteUser(Integer userId) throws BMSCheckedException;

    ResponseEntity<Object> getUserDetails(Integer userId);

    ResponseEntity<Object> confirmUserEmail(String uuid) throws BMSCheckedException;

    ResponseEntity<Object> getUserAddress();

    ResponseEntity<Object> updateUserAddress(AddressDto address);

    ResponseEntity<Object> resetPassword(Map<String, Object> requestBody) throws BMSCheckedException;

    ResponseEntity<Object> getLoggedInUserDetails();

    ResponseEntity<Object> getAllUsers();

    ResponseEntity<Object> getAllDrivers();

    ResponseEntity<Object> getAllAdmins();
}
