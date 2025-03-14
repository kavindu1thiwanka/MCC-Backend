package com.bms.service;

import com.bms.dto.AuthRequestDto;
import com.bms.exception.BusinessException;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<Object> authenticateUser(AuthRequestDto authRequest);

    ResponseEntity<Object> refreshToken(AuthRequestDto request);

    ResponseEntity<Object> sendPasswordResetMail(String email) throws BusinessException;
}
