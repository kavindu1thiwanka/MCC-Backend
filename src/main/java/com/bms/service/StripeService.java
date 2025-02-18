package com.bms.service;

import com.stripe.exception.StripeException;

import java.util.Map;

public interface StripeService {

    Map<String, String> createCheckoutSession(Map<String, Object> requestBody) throws StripeException;
}
