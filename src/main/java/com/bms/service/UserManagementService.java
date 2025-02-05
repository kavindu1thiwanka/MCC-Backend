package com.bms.service;

import com.bms.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserManagementService {

    ResponseEntity<Object> createUser(UserDto user);

    ResponseEntity<Object> updateUser(UserDto user);

    ResponseEntity<Object> activateUser(Integer userId);

    ResponseEntity<Object> inactivateUser(Integer userId);

    ResponseEntity<Object> deleteUser(Integer userId);

    ResponseEntity<Object> getUserDetails(Integer userId);
}
