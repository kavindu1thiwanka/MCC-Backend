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

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.bms.util.CommonConstants.STATUS_COMPLETE;

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

            PDPage page = new PDPage(new PDRectangle(PDRectangle.A3.getHeight(), PDRectangle.A3.getWidth()));
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(500, 750);
            contentStream.showText("Reservations Report");
            contentStream.endText();

            float margin = 50;
            float yStart = 750 - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float rowHeight = 20;

            // Adjust column widths for landscape mode
            float[] columnWidths = {80, 90, 150, 80, 150, 150, 130, 130, 80, 80};
            String[] headers = {"ID", "Vehicle No", "Customer Name", "Customer Id", "Pickup Location", "Drop-off Location", "Pickup Date", "Drop-off Date", "Total Price", "Status"};

            // Draw table header
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            yPosition -= rowHeight;
            drawTableRow(contentStream, yPosition, margin, tableWidth, columnWidths, headers);

            // Draw table rows
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            for (ReservationDto reservation : reservations) {
                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = yStart - rowHeight;
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    drawTableRow(contentStream, yPosition, margin, tableWidth, columnWidths, headers);
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                }

                yPosition -= rowHeight;
                String[] rowData = {
                        "#" + reservation.getId(),
                        reservation.getVehicleNo(),
                        reservation.getCustomerDetails().getFirstName() + " " + reservation.getCustomerDetails().getLastName(),
                        String.valueOf(reservation.getCustomerDetails().getId()),
                        reservation.getPickUpLocation(),
                        reservation.getReturnLocation(),
                        reservation.getPickUpDate().toString(),
                        reservation.getReturnDate().toString(),
                        String.valueOf(reservation.getTotalCost()),
                        reservation.getStatus().equals(STATUS_COMPLETE) ? "Completed" : "Cancelled"
                };
                drawTableRow(contentStream, yPosition, margin, tableWidth, columnWidths, rowData);
            }

            contentStream.close();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating reservations PDF", e);
        }
    }


    /**
     * Draws a table row with data.
     */
    private void drawTableRow(PDPageContentStream contentStream, float y, float margin, float tableWidth, float[] columnWidths, String[] data) throws IOException {
        float x = margin;
        float cellHeight = 20;

        // Draw cell background and text
        for (int i = 0; i < data.length; i++) {
            // Draw cell border
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(x, y, columnWidths[i], cellHeight);
            contentStream.stroke();

            // Add text inside the cell
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(x + 5, y + 5);
            contentStream.showText(data[i]);
            contentStream.endText();

            x += columnWidths[i];
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
