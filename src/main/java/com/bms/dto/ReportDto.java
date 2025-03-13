package com.bms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ReportDto {

    private String reportType;
    private Date startDate;
    private Date endDate;
    private String fileFormat;
    private String fileName;
    private byte[] fileContent;
}
