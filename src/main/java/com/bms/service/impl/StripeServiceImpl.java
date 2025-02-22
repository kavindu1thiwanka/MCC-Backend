package com.bms.service.impl;

import com.bms.service.StripeService;
import com.bms.util.CommonConstants;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.bms.util.CommonConstants.*;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${application.stripe.api.key}")
    private String stripeApiKey;

    @Value("${application.stripe.url.success}")
    private String successUrl;

    @Value("${application.stripe.url.cancel}")
    private String cancelUrl;

    @Override
    public ResponseEntity<Map<String, String>> createCheckoutSession(Map<String, Object> requestBody) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        Long amount = Long.parseLong(requestBody.get(STRING_AMOUNT).toString());
        String currency = requestBody.get(STRING_CURRENCY).toString();
        String trxId = requestBody.get(STRING_TRANSACTION_ID).toString();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl.replace(CommonConstants.PARAM_ID, trxId))
                .setCancelUrl(cancelUrl.replace(CommonConstants.PARAM_ID, trxId))
                .setClientReferenceId(trxId)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amount * 100)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Cab Service Booking" )
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        Map<String, String> response = new HashMap<>();
        response.put("checkoutUrl", session.getUrl());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
