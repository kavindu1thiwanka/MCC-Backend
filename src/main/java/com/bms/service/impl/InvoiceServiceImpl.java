package com.bms.service.impl;

import com.bms.dto.ReservationDto;
import com.bms.service.InvoiceService;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

@Service
public class InvoiceServiceImpl implements InvoiceService {

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

                // 🔹 Thank You Message
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
}
