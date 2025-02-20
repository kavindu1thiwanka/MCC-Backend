package com.bms.service;

import com.bms.dto.AuthRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<Object> authenticateUser(AuthRequestDto authRequest);

    ResponseEntity<Object> refreshToken(AuthRequestDto request);
}
