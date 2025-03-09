package com.bms.service;

import com.bms.dto.ReservationDto;

public interface InvoiceService {

    byte[] generateInvoice(ReservationDto reservationDto);

}
