package com.bms.service;

import com.bms.dto.ReportDto;
import com.bms.dto.ReservationDto;
import org.springframework.http.ResponseEntity;

public interface ReportService {

    ResponseEntity<Object> generateReport(ReportDto reportData);

    byte[] generateInvoice(ReservationDto reservationDto);
}
