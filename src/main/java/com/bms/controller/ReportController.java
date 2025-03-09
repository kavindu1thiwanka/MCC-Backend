package com.bms.controller;

import com.bms.dto.ReportDto;
import com.bms.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestScope
@RequestMapping(REPORT)
public class ReportController {

    private ReportService reportService;

    @PostMapping(GENERATE_REPORT)
    public ResponseEntity<Object> generateReport(@RequestBody ReportDto requestData) {
        return reportService.generateReport(requestData);
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
