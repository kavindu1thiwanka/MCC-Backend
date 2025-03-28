package com.bms.controller;

import com.bms.dto.ReservationDto;
import com.bms.exception.BusinessException;
import com.bms.service.ReservationManagementService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestScope
@RequestMapping(RESERVATION)
public class ReservationManagementController {

    private ReservationManagementService reservationManagementService;

    @PostMapping(CREATE_RESERVATION_V1)
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody ReservationDto reservationDto) throws BusinessException, StripeException {
        return reservationManagementService.createReservation(reservationDto);
    }

    @PutMapping(UPDATE_RESERVATION_DETAILS_V1)
    public ResponseEntity<Object> updateReservationDetails(@RequestParam Integer trxId, @RequestParam Character paymentStatus) throws BusinessException {
        return reservationManagementService.updateReservationDetails(trxId, paymentStatus);
    }

    @PutMapping(UPDATE_RESERVATION_STATUS_V1)
    public ResponseEntity<Object> updateReservationStatus(@RequestParam Integer reservationId, @RequestParam Character status) throws BusinessException {
        return reservationManagementService.updateReservationStatus(reservationId,status);
    }

    @GetMapping(GET_RESERVATION_DETAILS_V1)
    public ResponseEntity<Object> getLoggedInUserReservationDetails() throws BusinessException {
        return reservationManagementService.getLoggedInUserReservationDetails();
    }

    @GetMapping(GET_ACTIVE_RESERVATION_DETAILS_V1)
    public ResponseEntity<Object> getActiveReservationDetails() throws BusinessException {
        return reservationManagementService.getActiveReservationDetails();
    }

    @GetMapping(GET_RESERVATION_HISTORY_DETAILS_V1)
    public ResponseEntity<Object> getReservationHistoryDetails() throws BusinessException {
        return reservationManagementService.getReservationHistoryDetails();
    }

    @GetMapping(GET_RESERVATION_DETAILS_BY_ID_V1)
    public ResponseEntity<Object> getReservationDetails(@RequestParam Integer reservationId) throws BusinessException {
        return reservationManagementService.getReservationDetails(reservationId);
    }

    @PutMapping(CHANGE_ON_TRIP_STATUS_V1)
    public ResponseEntity<Object> changeOnTripStatus(@RequestParam Integer reservationId) throws BusinessException {
        return reservationManagementService.changeOnTripStatus(reservationId);
    }

    @Autowired
    public void setReservationManagementService(ReservationManagementService reservationManagementService) {
        this.reservationManagementService = reservationManagementService;
    }
}
