package com.bms.controller;

import com.bms.service.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

import static com.bms.controller.abst.Mappings.CREATE_PAYMENT_SESSION;
import static com.bms.controller.abst.Mappings.PAYMENTS;

@RestController
@RequestMapping(PAYMENTS)
//@CrossOrigin("*")
@RequestScope
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping(CREATE_PAYMENT_SESSION)
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> requestBody) throws StripeException {
        return stripeService.createCheckoutSession(requestBody);
    }
}
