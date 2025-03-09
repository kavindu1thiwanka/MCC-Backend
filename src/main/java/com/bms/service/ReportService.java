package com.bms.service;

import com.bms.dto.ReportDto;
import org.springframework.http.ResponseEntity;

public interface ReportService {

    ResponseEntity<Object> generateReport(ReportDto reportData);
}
