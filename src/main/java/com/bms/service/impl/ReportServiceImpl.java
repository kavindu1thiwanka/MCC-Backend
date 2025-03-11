package com.bms.service.impl;

import com.bms.dto.ReportDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.TransactionDto;
import com.bms.entity.TransactionMst;
import com.bms.entity.UserMst;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.TransactionMstRepository;
import com.bms.service.ReportService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bms.util.CommonConstants.STATUS_COMPLETE;

@Service
public class ReportServiceImpl implements ReportService {

    private ReservationMstRepository reservationMstRepository;
    private TransactionMstRepository transactionMstRepository;

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
                return generateRevenueReport(reportData);
            case "customer":
                return new ResponseEntity<>("Customer Report", HttpStatus.OK);
            case "vehicleUtilization":
                return new ResponseEntity<>("Vehicle Utilization Report", HttpStatus.OK);
            default:
                return new ResponseEntity<>("Invalid Report Type", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<Object> generateReservationReport(ReportDto reportData) {

        List<ReservationDto> reservationDetailsList = new ArrayList<>();

        if (reportData.getStartDate() == null || reportData.getEndDate() == null) {
            reservationDetailsList = reservationMstRepository.getReservationDetails();
        } else {
            reservationDetailsList =  reservationMstRepository.getReservationDetailsByDate(reportData.getStartDate(), reportData.getEndDate());
        }


        if (reservationDetailsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        if (reportData.getFileFormat().equals("pdf")) {
            reportData.setFileContent(generateReservationsPdf(reservationDetailsList, reportData));
        } else {
            reportData.setFileContent(generateReservationsExcel(reservationDetailsList));
        }

        reportData.setFileName("Reservations Details Report." + reportData.getFileFormat());

        return new ResponseEntity<>(reportData, HttpStatus.OK);
    }

    private byte[] generateReservationsPdf(List<ReservationDto> reservations, ReportDto reportData) {
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

            addMetadata(contentStream, reportData.getStartDate(), reportData.getEndDate(), 750, 900);

            float margin = 50;
            float yStart = 700 - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float rowHeight = 20;

            // Adjust column widths for landscape mode
            float[] columnWidths = {80, 90, 150, 80, 150, 150, 130, 130, 80, 80};
            String[] headers = {"ID", "Vehicle No", "Customer Name", "Customer ID", "Pickup Location", "Drop-off Location", "Pickup Date", "Drop-off Date", "Total Price", "Status"};

            // Draw table header
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            yPosition -= rowHeight;
            drawTableRow(contentStream, yPosition, margin, tableWidth, columnWidths, headers);

            // Draw table rows
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            for (ReservationDto reservation : reservations) {
                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage(new PDRectangle(PDRectangle.A3.getHeight(), PDRectangle.A3.getWidth()));
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

    private byte[] generateReservationsExcel(List<ReservationDto> reservations) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reservations");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Vehicle No");
            headerRow.createCell(2).setCellValue("Customer Name");
            headerRow.createCell(3).setCellValue("Customer ID");
            headerRow.createCell(4).setCellValue("Pickup Location");
            headerRow.createCell(5).setCellValue("Drop-off Location");
            headerRow.createCell(6).setCellValue("Pickup Date");
            headerRow.createCell(7).setCellValue("Drop-off Date");
            headerRow.createCell(8).setCellValue("Total Price");
            headerRow.createCell(9).setCellValue("Status");
            int rowIdx = 1;
            for (ReservationDto reservation : reservations) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("#" + reservation.getId());
                row.createCell(1).setCellValue(reservation.getVehicleNo());
                row.createCell(2).setCellValue(reservation.getCustomerDetails().getFirstName() + " " + reservation.getCustomerDetails().getLastName());
                row.createCell(3).setCellValue(String.valueOf(reservation.getCustomerDetails().getId()));
                row.createCell(4).setCellValue(reservation.getPickUpLocation());
                row.createCell(5).setCellValue(reservation.getReturnLocation());
                row.createCell(6).setCellValue(reservation.getPickUpDate().toString());
                row.createCell(7).setCellValue(reservation.getReturnDate().toString());
                row.createCell(8).setCellValue(String.valueOf(reservation.getTotalCost()));
                row.createCell(9).setCellValue(reservation.getStatus().equals(STATUS_COMPLETE) ? "Completed" : "Cancelled");
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating reservations Excel", e);
        }
    }

    private ResponseEntity<Object> generateRevenueReport(ReportDto reportData) {

        List<TransactionMst> trxDetailsList = new ArrayList<>();

        if (reportData.getStartDate() == null || reportData.getEndDate() == null) {
            trxDetailsList = transactionMstRepository.getTransactionDetails();
        } else {
            trxDetailsList = transactionMstRepository.getTransactionDetailsByDate(reportData.getStartDate(), reportData.getEndDate());
        }

        List<TransactionDto> transactionList = new ArrayList<>();
        String currentDate = null;
        TransactionDto transactionDto = null;

        for (TransactionMst trx : trxDetailsList) {
            if (currentDate == null || !currentDate.equals(trx.getPaymentDate().toString().split(" ")[0])) {
                currentDate = trx.getPaymentDate().toString().split(" ")[0];
                transactionDto = new TransactionDto(currentDate);
                transactionList.add(transactionDto);
            }

            transactionDto.setReservationCount(transactionDto.getReservationCount() + 1);
            transactionDto.setTotalAmount(transactionDto.getTotalAmount().add(trx.getAmount()));
        }

        if (trxDetailsList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        if (reportData.getFileFormat().equals("pdf")) {
            reportData.setFileContent(generateRevenuePdf(transactionList, reportData));
        } else {
            reportData.setFileContent(generateRevenueExcel(transactionList));
        }

        reportData.setFileName("Reservations Details Report." + reportData.getFileFormat());

        return new ResponseEntity<>(reportData, HttpStatus.OK);
    }

    private byte[] generateRevenuePdf(List<TransactionDto> transactionList, ReportDto reportData) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(220, 750);
            contentStream.showText("Revenue Report");
            contentStream.endText();

            addMetadata(contentStream, reportData.getStartDate(), reportData.getEndDate(), 700, 300);

            float margin = 50;
            float yStart = 680 - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float rowHeight = 20;

            // Adjust column widths for landscape mode
            float[] columnWidths = {200, 100, 200};
            String[] headers = {"Date", "Total Bookings", "Total Revenue (LKR)"};

            // Draw table header
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            yPosition -= rowHeight;
            drawTableRow(contentStream, yPosition, margin, tableWidth, columnWidths, headers);

            // Draw table rows
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            for (TransactionDto trx : transactionList) {
                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = yStart - rowHeight;
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    drawTableRow(contentStream, yPosition, margin, tableWidth, columnWidths, headers);
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                }

                yPosition -= rowHeight;
                String[] rowData = {
                        trx.getTransactionDate().toString(),
                        trx.getReservationCount().toString(),
                        String.valueOf(trx.getTotalAmount())
                };
                drawTableRow(contentStream, yPosition, margin, tableWidth, columnWidths, rowData);
            }

            contentStream.close();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating revenue PDF", e);
        }
    }

    private byte[] generateRevenueExcel(List<TransactionDto> transactionList) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reservations");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Total Bookings");
            headerRow.createCell(2).setCellValue("Total Revenue (LKR)");
            int rowIdx = 1;
            for (TransactionDto trx : transactionList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(trx.getTransactionDate());
                row.createCell(1).setCellValue(trx.getReservationCount());
                row.createCell(2).setCellValue(String.valueOf(trx.getTotalAmount()));
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating revenue Excel", e);
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

    private void addMetadata(PDPageContentStream contentStream, Date startDate, Date endDate, float ty, float tx) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        String dateRange = "Date Range: " + (startDate != null ? startDate : "N/A") +
                " - " + (endDate != null ? endDate : "N/A");

        String generatedDateTime = "Generated On: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String generatedBy = "Generated By: " + user.getFirstName() + " " + user.getLastName() + " (" + user.getUsername() + ")";

        float textYPos = ty;
        contentStream.beginText();
        contentStream.newLineAtOffset(tx, textYPos);
        contentStream.showText(dateRange);
        contentStream.endText();

        textYPos -= 20;
        contentStream.beginText();
        contentStream.newLineAtOffset(tx, textYPos);
        contentStream.showText(generatedDateTime);
        contentStream.endText();

        textYPos -= 20;
        contentStream.beginText();
        contentStream.newLineAtOffset(tx, textYPos);
        contentStream.showText(generatedBy);
        contentStream.endText();
    }

    /**
     * This method is used to generate invoice
     */
    @Override
    public byte[] generateInvoice(ReservationDto reservationDto) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                String logoUrl = "https://storage.googleapis.com/mcc_bucket_1/public/Logo-png.png";
                PDImageXObject logo = loadImageFromUrl(logoUrl, document);
                if (logo != null) {
                    contentStream.drawImage(logo, 50, 750, 80, 50);
                }

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(150, 770);
                contentStream.showText("Mega City Cab");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(150, 750);
                contentStream.showText("123 Main Street, Colombo, Sri Lanka");
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Phone: +123 456 7890  |  Email: bms@info.com");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(230, 700);
                contentStream.showText("INVOICE");
                contentStream.endText();

                float startY = 650;
                float margin = 50;
                float tableWidth = 500;
                float rowHeight = 20;
                float yPosition = startY;

                String[][] data = {
                        {"Reservation ID", "#" + reservationDto.getId()},
                        {"Customer Name", reservationDto.getCustomerDetails().getFirstName() + " " + reservationDto.getCustomerDetails().getLastName()},
                        {"Vehicle", reservationDto.getVehicleModel()},
                        {"With Driver", Boolean.TRUE.equals(reservationDto.getNeedDriver()) ? "Yes" : "No"},
                        {"Total Amount", "LKR " + reservationDto.getTotalCost()},
                };

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                for (String[] row : data) {
                    contentStream.setLineWidth(1f);
                    contentStream.addRect(margin, yPosition, tableWidth, rowHeight);
                    contentStream.stroke();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 5, yPosition + 5);
                    contentStream.showText(row[0]);
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin + 250, yPosition + 5);
                    contentStream.showText(row[1]);
                    contentStream.endText();
                    yPosition -= rowHeight;
                }

                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(200, yPosition - 40);
                contentStream.showText("Thank you for choosing MCC Vehicle Rentals!");
                contentStream.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }

    /**
     * Download image from URL and convert it to PDImageXObject.
     */
    private PDImageXObject loadImageFromUrl(String imageUrl, PDDocument document) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new URL(imageUrl));
            if (bufferedImage != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                return PDImageXObject.createFromByteArray(document, baos.toByteArray(), "logo");
            }
        } catch (IOException e) {
            System.err.println("Failed to load image from URL: " + e.getMessage());
        }
        return null;
    }

    @Autowired
    public void setReservationMstRepository(ReservationMstRepository reservationMstRepository) {
        this.reservationMstRepository = reservationMstRepository;
    }

    @Autowired
    public void setTransactionMstRepository(TransactionMstRepository transactionMstRepository) {
        this.transactionMstRepository = transactionMstRepository;
    }
}
