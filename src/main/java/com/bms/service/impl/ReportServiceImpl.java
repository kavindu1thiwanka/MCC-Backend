package com.bms.service.impl;

import com.bms.dto.ReportDto;
import com.bms.dto.ReservationDto;
import com.bms.service.ReportService;
import com.bms.service.ReservationManagementService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private ReservationManagementService reservationManagementService;

    /**
     * This method is used to generate report
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> generateReport(ReportDto reportData) {
        switch (reportData.getReportType()) {
            case "reservations":
                return generateReservationReport(reportData);
            case "revenue":
                return new ResponseEntity<>("Payment Report", HttpStatus.OK);
            case "customer":
                return new ResponseEntity<>("Customer Report", HttpStatus.OK);
            case "vehicleUtilization":
                return new ResponseEntity<>("Vehicle Utilization Report", HttpStatus.OK);
            default:
                return new ResponseEntity<>("Invalid Report Type", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<Object> generateReservationReport(ReportDto reportData) {

        List<ReservationDto> reservationDetailsList = reservationManagementService.getReservationDetailsList(reportData);

        if (reservationDetailsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        if (reportData.getFileFormat().equals("pdf")) {
            reportData.setFileContent(generateReservationsPdf(reservationDetailsList));
        } else {
            reportData.setFileContent(generateReservationsExcel(reservationDetailsList));
        }

        reportData.setFileName("Reservations Details Report." + reportData.getFileFormat());

        return new ResponseEntity<>(reportData, HttpStatus.OK);
    }

    private byte[] generateReservationsPdf(List<ReservationDto> reservations) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(200, 750);
                contentStream.showText("Reservations Report");
                contentStream.endText();

                float yPosition = 700;
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                for (ReservationDto reservation : reservations) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText("Reservation ID: " + reservation.getId() + " | Customer: " +" reservation.getCustomer().getFullName()");
                    contentStream.endText();
                    yPosition -= 20;
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating reservations PDF", e);
        }
    }

    private byte[] generateReservationsExcel(List<ReservationDto> reservations) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reservations");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Reservation ID");
            headerRow.createCell(1).setCellValue("Customer");
            headerRow.createCell(2).setCellValue("Vehicle");

            int rowIdx = 1;
            for (ReservationDto reservation : reservations) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(reservation.getId());
//                row.createCell(1).setCellValue(reservation.getCustomer().getFullName());
//                row.createCell(2).setCellValue(reservation.getVehicle().getModel());
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating reservations Excel", e);
        }
    }

    @Autowired
    public void setReservationManagementService(ReservationManagementService reservationManagementService) {
        this.reservationManagementService = reservationManagementService;
    }
}
