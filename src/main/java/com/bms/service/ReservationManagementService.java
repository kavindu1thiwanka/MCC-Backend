package com.bms.service;

import com.bms.dto.ReservationDto;
import com.bms.util.BMSCheckedException;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ReservationManagementService {

    ResponseEntity<Map<String, String>> createReservation(ReservationDto reservationDto) throws BMSCheckedException, StripeException;

    ResponseEntity<Object> updateReservationDetails(Integer trxId, Character paymentStatus) throws BMSCheckedException;

    ResponseEntity<Object> getLoggedInUserReservationDetails();

    ResponseEntity<Object> updateReservationStatus(Integer reservationId, Character status) throws BMSCheckedException;

    ResponseEntity<Object> getActiveReservationDetails() throws BMSCheckedException;

    ResponseEntity<Object> getReservationHistoryDetails() throws BMSCheckedException;

    ResponseEntity<Object> getReservationDetails(Integer reservationId);
}
