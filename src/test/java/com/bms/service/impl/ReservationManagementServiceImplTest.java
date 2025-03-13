package com.bms.service.impl;

import com.bms.dto.ReservationDto;
import com.bms.entity.*;
import com.bms.exception.BusinessException;
import com.bms.repository.*;
import com.bms.service.EmailService;
import com.bms.service.ReportService;
import com.bms.service.StripeService;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
class ReservationManagementServiceImplTest {

    @Mock
    private ReservationMstRepository reservationMstRepository;

    @Mock
    private UserMstRepository userMstRepository;

    @Mock
    private TransactionMstRepository transactionMstRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private StripeService stripeService;

    @Mock
    private CommonEmailTemplateRepository commonEmailTemplateRepository;

    @Mock
    private VehicleMstRepository vehicleMstRepository;

    @Mock
    private CommonEmailMstRepository commonEmailMstRepository;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReservationManagementServiceImpl reservationService;

    private UserMst testUser;
    private ReservationDto testReservationDto;
    private ReservationMst testReservation;
    private TransactionMst testTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestUser();
        setupTestData();
    }

    @Test
    void createReservation_Success() throws BusinessException, StripeException {
        // Arrange
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("sessionId", "test_session");
        when(stripeService.createCheckoutSession(any())).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));
        when(reservationMstRepository.save(any())).thenReturn(testReservation);
        when(transactionMstRepository.save(any())).thenReturn(testTransaction);

        // Act
        ResponseEntity<Map<String, String>> response = reservationService.createReservation(testReservationDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test_session", response.getBody().get("sessionId"));
        verify(reservationMstRepository).save(any(ReservationMst.class));
        verify(transactionMstRepository).save(any(TransactionMst.class));
    }

    @Test
    void createReservation_WithDriver_Success() throws BusinessException, StripeException {
        // Arrange
        testReservationDto.setNeedDriver(true);
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("sessionId", "test_session");
        
        UserMst driver = new UserMst();
        driver.setId(2);
        driver.setRoleId(ROLE_ID_DRIVER);
        driver.setIsOnline(STATUS_YES);
        driver.setStatus(STATUS_ACTIVE);
        
        when(userMstRepository.getAllAvailableDrivers()).thenReturn(Arrays.asList(driver));
        when(stripeService.createCheckoutSession(any())).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));
        when(reservationMstRepository.save(any())).thenReturn(testReservation);
        when(transactionMstRepository.save(any())).thenReturn(testTransaction);

        // Act
        ResponseEntity<Map<String, String>> response = reservationService.createReservation(testReservationDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userMstRepository).getAllAvailableDrivers();
    }

    @Test
    void createReservation_NoDriverAvailable() {
        // Arrange
        testReservationDto.setNeedDriver(true);
        when(userMstRepository.getAllAvailableDrivers()).thenReturn(Collections.emptyList());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> reservationService.createReservation(testReservationDto));
        assertEquals(DRIVERS_NOT_AVAILABLE, exception.getMessage());
    }

    @Test
    void createReservation_InvalidDates() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        testReservationDto.setPickUpDate(cal.getTime());
        cal.add(Calendar.DATE, -1);
        testReservationDto.setReturnDate(cal.getTime());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> reservationService.createReservation(testReservationDto));
        assertEquals(INVALID_DATE, exception.getMessage());
    }

    @Test
    void updateReservationDetails_Success() throws BusinessException {
        // Arrange
        when(transactionMstRepository.findById(1)).thenReturn(Optional.of(testTransaction));
        when(reservationMstRepository.findById(1)).thenReturn(Optional.of(testReservation));
        when(userMstRepository.findById(any())).thenReturn(Optional.of(testUser));
        when(vehicleMstRepository.getVehicleModelByVehicleNumber(any())).thenReturn("Test Model");
        when(commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_RESERVATION_SUCCESSFUL))
            .thenReturn(Optional.of(createTestEmailTemplate()));

        // Act
        ResponseEntity<Object> response = reservationService.updateReservationDetails(1, STATUS_COMPLETE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(transactionMstRepository).save(any(TransactionMst.class));
        verify(reservationMstRepository).save(any(ReservationMst.class));
        verify(emailService).sendEmailWithAttachment(any(), any());
    }

    @Test
    void updateReservationDetails_TransactionNotFound() {
        // Arrange
        when(transactionMstRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> reservationService.updateReservationDetails(1, STATUS_COMPLETE));
        assertEquals(TRANSACTION_NOT_FOUND, exception.getMessage());
    }

    @Test
    void updateReservationDetails_EmailTemplateMissing() {
        // Arrange
        testReservation.setStatus(STATUS_NOT_PAID);
        testTransaction.setStatus(STATUS_TRANSACTION_PENDING);
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        
        when(transactionMstRepository.findById(1)).thenReturn(Optional.of(testTransaction));
        when(reservationMstRepository.findById(1)).thenReturn(Optional.of(testReservation));
        when(userMstRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(vehicleMstRepository.getVehicleModelByVehicleNumber(any())).thenReturn("Test Model");
        when(commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_RESERVATION_SUCCESSFUL))
            .thenReturn(Optional.empty());
        when(reportService.generateInvoice(any())).thenReturn(new byte[0]);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> reservationService.updateReservationDetails(1, STATUS_COMPLETE));
        assertEquals(EMAIL_TEMPLATE_NOT_FOUND, exception.getMessage());
        
        // Verify that we attempted to get the email template
        verify(commonEmailTemplateRepository).findById(EMAIL_TEMPLATE_RESERVATION_SUCCESSFUL);
        // Verify we never got to sending the email since template was missing
        verify(emailService, never()).sendEmailWithAttachment(any(), any());
    }

    @Test
    void getLoggedInUserReservationDetails_Success() {
        // Arrange
        List<ReservationMst> mockReservations = Arrays.asList(testReservation);
        when(reservationMstRepository.getReservationDetailsByCreatedUser(testUser.getUsername()))
            .thenReturn(mockReservations);

        // Act
        ResponseEntity<Object> response = reservationService.getLoggedInUserReservationDetails();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updateReservationStatus_Success() throws BusinessException {
        // Arrange
        when(reservationMstRepository.findById(1)).thenReturn(Optional.of(testReservation));

        // Act
        ResponseEntity<Object> response = reservationService.updateReservationStatus(1, STATUS_COMPLETE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reservationMstRepository).save(any(ReservationMst.class));
    }

    @Test
    void updateReservationStatus_NotFound() {
        // Arrange
        when(reservationMstRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> reservationService.updateReservationStatus(1, STATUS_COMPLETE));
        assertEquals(RESERVATION_NOT_FOUND, exception.getMessage());
    }

    @Test
    void getActiveReservationDetails_Success() throws BusinessException {
        // Arrange
        List<ReservationMst> mockReservations = Arrays.asList(testReservation);
        when(reservationMstRepository.getReservationDetailsByStatus(any()))
            .thenReturn(mockReservations);

        // Act
        ResponseEntity<Object> response = reservationService.getActiveReservationDetails();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void changeOnTripStatus_InvalidStatus() throws BusinessException {
        // Arrange
        testReservation.setStatus(STATUS_COMPLETE);
        when(reservationMstRepository.findById(1)).thenReturn(Optional.of(testReservation));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> reservationService.changeOnTripStatus(1));
        assertEquals(COMPLETED_OR_CANCELLED_RESERVATIONS_STATUS_CANNOT_BE_UPDATED, exception.getMessage());
    }

    @Test
    void updateReservationDetails_SynchronizesStatuses() {
        // Arrange
        testReservation.setStatus(STATUS_NOT_PAID);
        testTransaction.setStatus(STATUS_TRANSACTION_PENDING);
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        
        when(transactionMstRepository.findById(1)).thenReturn(Optional.of(testTransaction));
        when(reservationMstRepository.findById(1)).thenReturn(Optional.of(testReservation));
        when(userMstRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(vehicleMstRepository.getVehicleModelByVehicleNumber(any())).thenReturn("Test Model");
        when(commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_RESERVATION_SUCCESSFUL))
            .thenReturn(Optional.of(createTestEmailTemplate()));
        when(reportService.generateInvoice(any())).thenReturn(new byte[0]);

        // Act
        ResponseEntity<Object> response = reservationService.updateReservationDetails(1, STATUS_COMPLETE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify transaction status update
        ArgumentCaptor<TransactionMst> transactionCaptor = ArgumentCaptor.forClass(TransactionMst.class);
        verify(transactionMstRepository).save(transactionCaptor.capture());
        assertEquals(STATUS_COMPLETE, transactionCaptor.getValue().getStatus());
        assertNotNull(transactionCaptor.getValue().getUpdateOn());
        assertEquals(testUser.getUsername(), transactionCaptor.getValue().getUpdateBy());
        
        // Verify reservation status update
        ArgumentCaptor<ReservationMst> reservationCaptor = ArgumentCaptor.forClass(ReservationMst.class);
        verify(reservationMstRepository).save(reservationCaptor.capture());
        assertEquals(STATUS_COMPLETE, reservationCaptor.getValue().getPaymentStatus());
        assertEquals(STATUS_ACTIVE, reservationCaptor.getValue().getStatus());
        assertNotNull(reservationCaptor.getValue().getUpdateOn());
        assertEquals(testUser.getUsername(), reservationCaptor.getValue().getUpdateBy());
        
        // Verify email was sent
        verify(emailService).sendEmailWithAttachment(any(), any());
    }

    @Test
    void updateReservationDetails_PaymentFailed() {
        // Arrange
        testReservation.setStatus(STATUS_NOT_PAID);
        testTransaction.setStatus(STATUS_TRANSACTION_PENDING);
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        
        when(transactionMstRepository.findById(1)).thenReturn(Optional.of(testTransaction));
        when(reservationMstRepository.findById(1)).thenReturn(Optional.of(testReservation));
        when(userMstRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Object> response = reservationService.updateReservationDetails(1, STATUS_FAILED);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify transaction status update
        ArgumentCaptor<TransactionMst> transactionCaptor = ArgumentCaptor.forClass(TransactionMst.class);
        verify(transactionMstRepository).save(transactionCaptor.capture());
        assertEquals(STATUS_FAILED, transactionCaptor.getValue().getStatus());
        assertNotNull(transactionCaptor.getValue().getUpdateOn());
        assertEquals(testUser.getUsername(), transactionCaptor.getValue().getUpdateBy());
        
        // Verify reservation status update
        ArgumentCaptor<ReservationMst> reservationCaptor = ArgumentCaptor.forClass(ReservationMst.class);
        verify(reservationMstRepository).save(reservationCaptor.capture());
        assertEquals(STATUS_FAILED, reservationCaptor.getValue().getPaymentStatus());
        assertEquals(STATUS_FAILED, reservationCaptor.getValue().getStatus());
        assertNotNull(reservationCaptor.getValue().getUpdateOn());
        assertEquals(testUser.getUsername(), reservationCaptor.getValue().getUpdateBy());
        
        // Verify no email was sent since payment failed
        verify(emailService, never()).sendEmailWithAttachment(any(), any());
        verify(commonEmailTemplateRepository, never()).findById(any());
    }

    private void setupTestUser() {
        testUser = new UserMst();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setStatus(STATUS_ACTIVE);
        testUser.setRoleId(ROLE_ID_CUSTOMER);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
    }

    private void setupTestData() {
        // Setup ReservationDto
        testReservationDto = new ReservationDto();
        testReservationDto.setId(1);
        testReservationDto.setVehicleNo("ABC123");
        testReservationDto.setVehicleModel("Test Model");
        testReservationDto.setPickUpLocation("Test Location");
        testReservationDto.setReturnLocation("Return Location");
        testReservationDto.setPickUpDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        testReservationDto.setReturnDate(cal.getTime());
        testReservationDto.setTotalCost(BigDecimal.valueOf(1000));
        testReservationDto.setNeedDriver(false);
        testReservationDto.setStatus(STATUS_ACTIVE);
        testReservationDto.setCustomerDetails(testUser);

        // Setup ReservationMst
        testReservation = new ReservationMst(testReservationDto);
        testReservation.setId(1);
        testReservation.setUserId(testUser.getId());
        testReservation.setCreatedBy(testUser.getEmail());
        testReservation.setCreatedOn(new Date());
        testReservation.setStatus(STATUS_ACTIVE);

        // Setup TransactionMst
        testTransaction = new TransactionMst();
        testTransaction.setId(1);
        testTransaction.setReservationId(testReservation.getId());
        testTransaction.setAmount(testReservationDto.getTotalCost());
        testTransaction.setPaymentType(PAYMENT_TYPE_CARD);
        testTransaction.setStatus(STATUS_TRANSACTION_PENDING);
    }

    private CommonEmailTemplate createTestEmailTemplate() {
        CommonEmailTemplate template = new CommonEmailTemplate();
        template.setId(EMAIL_TEMPLATE_RESERVATION_SUCCESSFUL);
        template.setTemplateData("""
            <html>
                <body>
                    <div id="EMAIL_SEND_TO"></div>
                    <div id="RESERVATION_ID"></div>
                    <div id="VEHICLE_MODEL"></div>
                    <div id="PICKUP_LOCATION"></div>
                    <div id="DROPOFF_LOCATION"></div>
                    <div id="PICKUP_DATE"></div>
                    <div id="DROPOFF_DATE"></div>
                    <div id="TOTAL_PRICE"></div>
                </body>
            </html>
            """);
        template.setSubject("Reservation Confirmation");
        return template;
    }
}
