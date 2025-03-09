package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class TransactionDto {

    private String transactionDate;
    private Integer reservationCount = 0;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public TransactionDto(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
