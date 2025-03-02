package com.bms.controller;

import com.bms.dto.ReservationDto;
import com.bms.service.ReservationManagementService;
import com.bms.util.BMSCheckedException;
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
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody ReservationDto reservationDto) throws BMSCheckedException, StripeException {
        return reservationManagementService.createReservation(reservationDto);
    }

    @PutMapping(UPDATE_RESERVATION_DETAILS_V1)
    public ResponseEntity<Object> updateReservationDetails(@RequestParam Integer trxId, @RequestParam Character paymentStatus) throws BMSCheckedException {
        return reservationManagementService.updateReservationDetails(trxId, paymentStatus);
    }

    @PutMapping(UPDATE_RESERVATION_STATUS_V1)
    public ResponseEntity<Object> updateReservationStatus(@RequestParam Integer reservationId, @RequestParam Character status) throws BMSCheckedException {
        return reservationManagementService.updateReservationStatus(reservationId,status);
    }

    @GetMapping(GET_RESERVATION_DETAILS_V1)
    public ResponseEntity<Object> getReservationDetails() throws BMSCheckedException {
        return reservationManagementService.getReservationDetails();
    }

    @Autowired
    public void setReservationManagementService(ReservationManagementService reservationManagementService) {
        this.reservationManagementService = reservationManagementService;
    }
}
