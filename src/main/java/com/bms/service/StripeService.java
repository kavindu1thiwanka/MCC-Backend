package com.bms.service;

import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface StripeService {

    ResponseEntity<Map<String, String>> createCheckoutSession(Map<String, Object> requestBody) throws StripeException;
}
