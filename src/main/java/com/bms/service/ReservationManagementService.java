package com.bms.service;

import com.bms.dto.ReservationDto;
import com.bms.util.BMSCheckedException;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ReservationManagementService {

    ResponseEntity<Map<String, String>> createReservation(ReservationDto reservationDto) throws BMSCheckedException, StripeException;

    ResponseEntity<Object> updateReservationDetails(Integer trxId, Character paymentStatus) throws BMSCheckedException;

    ResponseEntity<Object> getReservationDetails();
}
