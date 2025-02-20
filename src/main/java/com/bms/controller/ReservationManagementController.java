package com.bms.controller;

import com.bms.dto.ReservationDto;
import com.bms.service.ReservationManagementService;
import com.bms.util.BMSCheckedException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import static com.bms.controller.abst.Mappings.CREATE_RESERVATION_V1;
import static com.bms.controller.abst.Mappings.RESERVATION;

@RestController
@RequestScope
@RequestMapping(RESERVATION)
public class ReservationManagementController {

    private ReservationManagementService reservationManagementService;

    @PostMapping(CREATE_RESERVATION_V1)
    public ResponseEntity<Object> createReservation(@RequestBody ReservationDto reservationDto) throws BMSCheckedException, StripeException {
        return reservationManagementService.createReservation(reservationDto);
    }

    @Autowired
    public void setReservationManagementService(ReservationManagementService reservationManagementService) {
        this.reservationManagementService = reservationManagementService;
    }
}
