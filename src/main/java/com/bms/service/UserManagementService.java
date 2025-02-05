package com.bms.service;

import com.bms.dto.UserDto;
import com.bms.util.BMSCheckedException;
import org.springframework.http.ResponseEntity;

public interface UserManagementService {

    ResponseEntity<Object> createUser(UserDto user) throws BMSCheckedException;

    ResponseEntity<Object> updateUser(UserDto user) throws BMSCheckedException;

    ResponseEntity<Object> activateUser(Integer userId) throws BMSCheckedException;

    ResponseEntity<Object> inactivateUser(Integer userId) throws BMSCheckedException;

    ResponseEntity<Object> deleteUser(Integer userId) throws BMSCheckedException;

    ResponseEntity<Object> getUserDetails(Integer userId);
}
