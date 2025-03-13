package com.bms.service;

import com.bms.dto.ReportDto;
import com.bms.dto.ReservationDto;
import com.bms.entity.TransactionMst;
import com.bms.exception.BusinessException;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ReservationManagementService {

    ResponseEntity<Map<String, String>> createReservation(ReservationDto reservationDto) throws BusinessException, StripeException;

    ResponseEntity<Object> updateReservationDetails(Integer trxId, Character paymentStatus) throws BusinessException;

    ResponseEntity<Object> getLoggedInUserReservationDetails();

    ResponseEntity<Object> updateReservationStatus(Integer reservationId, Character status) throws BusinessException;

    ResponseEntity<Object> getActiveReservationDetails() throws BusinessException;

    ResponseEntity<Object> getReservationHistoryDetails() throws BusinessException;

    ResponseEntity<Object> getReservationDetails(Integer reservationId);

    ResponseEntity<Object> changeOnTripStatus(Integer reservationId) throws BusinessException;

    List<ReservationDto> getReservationDetailsList(ReportDto reportData);

    List<TransactionMst> getTransactionDetailsList(ReportDto reportData);
}
