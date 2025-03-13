package com.bms.config;

import com.bms.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", ex.getStatus().value());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", "Access denied");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, HttpServletResponse response) {
        Map<String, Object> body = new HashMap<>();
        HttpStatus status = response.getStatus() != HttpServletResponse.SC_OK 
            ? HttpStatus.valueOf(response.getStatus()) 
            : HttpStatus.INTERNAL_SERVER_ERROR;
            
        body.put("status", status.value());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex, HttpServletResponse response) {
        Map<String, Object> body = new HashMap<>();
        HttpStatus status = response.getStatus() != HttpServletResponse.SC_OK 
            ? HttpStatus.valueOf(response.getStatus()) 
            : HttpStatus.INTERNAL_SERVER_ERROR;
            
        body.put("status", status.value());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }
} 