package com.bms.service.impl;

import com.bms.dto.ReportDto;
import com.bms.dto.ReservationDto;
import com.bms.entity.TransactionMst;
import com.bms.entity.UserMst;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.TransactionMstRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bms.util.CommonConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
class ReportServiceImplTest {

    @Mock
    private ReservationMstRepository reservationMstRepository;

    @Mock
    private TransactionMstRepository transactionMstRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private UserMst testUser;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestUser();
        setupTestDates();
    }

    @Test
    void generateReport_WithInvalidType_ShouldReturnBadRequest() {
        // Arrange
        ReportDto reportData = new ReportDto();
        reportData.setReportType("invalid");

        // Act
        ResponseEntity<Object> response = reportService.generateReport(reportData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Report Type", response.getBody());
    }

    @Test
    void generateReport_ReservationsAsPdf_ShouldGenerateCorrectly() {
        // Arrange
        ReportDto reportData = createReportDto("reservations", "pdf", startDate, endDate);
        List<ReservationDto> reservations = Arrays.asList(
            createTestReservationDto(1),
            createTestReservationDto(2)
        );
        when(reservationMstRepository.getReservationDetailsByDate(any(), any()))
            .thenReturn(reservations);

        // Act
        ResponseEntity<Object> response = reportService.generateReport(reportData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReportDto result = (ReportDto) response.getBody();
        assertNotNull(result);
        assertTrue(result.getFileName().endsWith(".pdf"));
        assertNotNull(result.getFileContent());
        
        // Verify PDF structure
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(result.getFileContent()))) {
            assertEquals(1, document.getNumberOfPages());
        } catch (IOException e) {
            fail("Failed to parse PDF: " + e.getMessage());
        }
    }

    @Test
    void generateReport_ReservationsAsExcel_ShouldGenerateCorrectly() {
        // Arrange
        ReportDto reportData = createReportDto("reservations", "xlsx", startDate, endDate);
        List<ReservationDto> reservations = Arrays.asList(
            createTestReservationDto(1),
            createTestReservationDto(2)
        );
        when(reservationMstRepository.getReservationDetailsByDate(any(), any()))
            .thenReturn(reservations);

        // Act
        ResponseEntity<Object> response = reportService.generateReport(reportData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReportDto result = (ReportDto) response.getBody();
        assertNotNull(result);
        assertTrue(result.getFileName().endsWith(".xlsx"));
        assertNotNull(result.getFileContent());
    }

    @Test
    void generateReport_RevenueAsPdf_ShouldGenerateCorrectly() {
        // Arrange
        ReportDto reportData = createReportDto("revenue", "pdf", startDate, endDate);
        List<TransactionMst> transactions = Arrays.asList(
            createTestTransaction(1, new Date()),
            createTestTransaction(2, new Date())
        );
        when(transactionMstRepository.getTransactionDetailsByDate(any(), any()))
            .thenReturn(transactions);

        // Act
        ResponseEntity<Object> response = reportService.generateReport(reportData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReportDto result = (ReportDto) response.getBody();
        assertNotNull(result);
        assertTrue(result.getFileName().endsWith(".pdf"));
        assertNotNull(result.getFileContent());
        
        // Verify PDF structure
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(result.getFileContent()))) {
            assertEquals(1, document.getNumberOfPages());
        } catch (IOException e) {
            fail("Failed to parse PDF: " + e.getMessage());
        }
    }

    @Test
    void generateReport_WithNoData_ShouldReturnEmptyResponse() {
        // Arrange
        ReportDto reportData = createReportDto("reservations", "pdf", startDate, endDate);
        when(reservationMstRepository.getReservationDetailsByDate(any(), any()))
            .thenReturn(Arrays.asList());

        // Act
        ResponseEntity<Object> response = reportService.generateReport(reportData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void generateInvoice_ShouldGenerateCorrectly() {
        // Arrange
        ReservationDto reservation = createTestReservationDto(1);

        // Act
        byte[] result = reportService.generateInvoice(reservation);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verify PDF structure
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(result))) {
            assertEquals(1, document.getNumberOfPages());
        } catch (IOException e) {
            fail("Failed to parse PDF: " + e.getMessage());
        }
    }

    private void setupTestUser() {
        testUser = new UserMst();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setContactNumber("1234567890");
        testUser.setDriverLicenseNo("DL123456");
        testUser.setStatus(STATUS_ACTIVE);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
    }

    private void setupTestDates() {
        Calendar cal = Calendar.getInstance();
        endDate = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        startDate = cal.getTime();
    }

    private ReportDto createReportDto(String reportType, String fileFormat, Date startDate, Date endDate) {
        ReportDto reportData = new ReportDto();
        reportData.setReportType(reportType);
        reportData.setFileFormat(fileFormat);
        reportData.setStartDate(startDate);
        reportData.setEndDate(endDate);
        return reportData;
    }

    private ReservationDto createTestReservationDto(Integer id) {
        ReservationDto reservation = new ReservationDto();
        reservation.setId(id);
        reservation.setVehicleNo("ABC-" + id);
        reservation.setVehicleModel("Model Name");
        reservation.setPickUpLocation("Location " + id);
        reservation.setReturnLocation("Return " + id);
        reservation.setPickUpDate(new Date());
        reservation.setReturnDate(new Date());
        reservation.setTotalCost(BigDecimal.valueOf(1000));
        reservation.setStatus(STATUS_COMPLETE);
        
        UserMst customer = new UserMst();
        customer.setId(id);
        customer.setFirstName("Customer");
        customer.setLastName(id.toString());
        customer.setUsername("customer" + id);
        customer.setEmail("customer" + id + "@example.com");
        customer.setContactNumber("1234567890");
        customer.setDriverLicenseNo("DL" + id);
        customer.setStatus(STATUS_ACTIVE);
        customer.setRoleId(ROLE_ID_CUSTOMER);
        reservation.setCustomerDetails(customer);
        
        return reservation;
    }

    private TransactionMst createTestTransaction(Integer id, Date date) {
        TransactionMst transaction = new TransactionMst();
        transaction.setId(id);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setPaymentDate(date);
        return transaction;
    }
}
