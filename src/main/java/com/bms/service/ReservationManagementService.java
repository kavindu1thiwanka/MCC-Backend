package com.bms.service;

import com.bms.dto.ReservationDto;
import com.bms.util.BMSCheckedException;
import org.springframework.http.ResponseEntity;

public interface ReservationManagementService {

    ResponseEntity<Object> createReservation(ReservationDto reservationDto) throws BMSCheckedException;
}
