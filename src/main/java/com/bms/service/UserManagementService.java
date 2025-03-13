package com.bms.service;

import com.bms.dto.AddressDto;
import com.bms.dto.UserDto;
import com.bms.exception.BusinessException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public interface UserManagementService {

    ResponseEntity<Object> registerUser(UserDto user) throws BusinessException;

    ResponseEntity<Object> createUser(UserDto user) throws BusinessException, IOException;

    ResponseEntity<Object> updateUser(UserDto user) throws BusinessException, IOException;

    ResponseEntity<Object> changeUserStatus(Integer userId, Character status) throws BusinessException;

    ResponseEntity<Object> getUserDetails(Integer userId);

    ResponseEntity<Object> confirmUserEmail(String uuid) throws BusinessException;

    ResponseEntity<Object> getUserAddress();

    ResponseEntity<Object> updateUserAddress(AddressDto address);

    ResponseEntity<Object> resetPassword(Map<String, Object> requestBody) throws BusinessException;

    ResponseEntity<Object> getLoggedInUserDetails();

    ResponseEntity<Object> getAllUsers();

    ResponseEntity<Object> getAllDrivers();

    ResponseEntity<Object> getAllAdmins();
}
