package com.bms.service;

import com.bms.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface UserManagementService {

    ResponseEntity<Object> createUser(UserDto user);
}
