package com.bms.service.impl;

import com.bms.entity.ReservationMst;
import com.bms.entity.TransactionMst;
import com.bms.entity.UserMst;
import com.bms.exception.BusinessException;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.TransactionMstRepository;
import com.bms.repository.UserMstRepository;
import org.junit.jupiter.api.AfterEach;
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

import java.math.BigDecimal;
import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
class DriverManagementServiceImplTest {

    @Mock
    private UserMstRepository userMstRepository;

    @Mock
    private TransactionMstRepository transactionMstRepository;

    @Mock
    private ReservationMstRepository reservationMstRepository;

    @InjectMocks
    private DriverManagementServiceImpl driverManagementService;

    private static final Integer DRIVER_ID = 1;
    private static final String DRIVER_USERNAME = "testdriver";
    private UserMst testDriver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDriver = createTestDriver();
        setupSecurityContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateOnlineStatus_WhenGoingOnline_ShouldUpdateStatus() throws BusinessException {
        when(userMstRepository.findById(DRIVER_ID)).thenReturn(Optional.of(testDriver));

        ResponseEntity<Object> response = driverManagementService.updateOnlineStatus(true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userMstRepository).save(argThat(driver -> 
            driver.getIsOnline() == STATUS_YES
        ));
    }

    @Test
    void updateOnlineStatus_WhenGoingOffline_ShouldUpdateStatus() throws BusinessException {
        when(userMstRepository.findById(DRIVER_ID)).thenReturn(Optional.of(testDriver));

        ResponseEntity<Object> response = driverManagementService.updateOnlineStatus(false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userMstRepository).save(argThat(driver -> 
            driver.getIsOnline() == STATUS_NO
        ));
    }

    @Test
    void updateOnlineStatus_WhenUserNotFound_ShouldThrowException() {
        when(userMstRepository.findById(DRIVER_ID)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> driverManagementService.updateOnlineStatus(true));

        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(userMstRepository, never()).save(any());
    }

    @Test
    void getDriverDashboardDetails_ShouldReturnCompleteStats() {
        // Mock online status
        when(userMstRepository.getDriverOnlineStatus(DRIVER_ID)).thenReturn(STATUS_YES);

        // Mock transactions for earnings calculation
        List<TransactionMst> transactions = createMockTransactions();
        when(transactionMstRepository.getDriverEarningsByDriverId(DRIVER_ID))
                .thenReturn(transactions);

        // Mock upcoming reservations
        List<ReservationMst> upcomingReservations = createMockUpcomingReservations();
        when(reservationMstRepository.getUpcomingReservationsByDriverId(any(Date.class), eq(DRIVER_ID)))
                .thenReturn(upcomingReservations);

        ResponseEntity<Object> response = driverManagementService.getDriverDashboardDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        
        // Verify online status
        assertTrue((Boolean) responseBody.get("isOnline"));

        // Verify earnings
        @SuppressWarnings("unchecked")
        HashMap<String, Object> earnings = (HashMap<String, Object>) responseBody.get("earnings");
        assertNotNull(earnings);
        assertEquals(new BigDecimal("2400"), earnings.get("total")); // 2 transactions * 1200
        
        // Verify upcoming rides
        @SuppressWarnings("unchecked")
        List<ReservationMst> rides = (List<ReservationMst>) responseBody.get("upcomingRides");
        assertEquals(2, rides.size());
    }

    @Test
    void getDriverDashboardDetails_WithNoData_ShouldReturnEmptyStats() {
        when(userMstRepository.getDriverOnlineStatus(DRIVER_ID)).thenReturn(STATUS_NO);
        when(transactionMstRepository.getDriverEarningsByDriverId(DRIVER_ID))
                .thenReturn(new ArrayList<>());
        when(reservationMstRepository.getUpcomingReservationsByDriverId(any(Date.class), eq(DRIVER_ID)))
                .thenReturn(new ArrayList<>());

        ResponseEntity<Object> response = driverManagementService.getDriverDashboardDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        assertFalse((Boolean) responseBody.get("isOnline"));
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> earnings = (HashMap<String, Object>) responseBody.get("earnings");
        assertEquals(BigDecimal.ZERO, earnings.get("total"));
        assertEquals(BigDecimal.ZERO, earnings.get("daily"));
        assertEquals(BigDecimal.ZERO, earnings.get("weekly"));
        assertEquals(BigDecimal.ZERO, earnings.get("monthly"));
        
        @SuppressWarnings("unchecked")
        List<ReservationMst> rides = (List<ReservationMst>) responseBody.get("upcomingRides");
        assertTrue(rides.isEmpty());
    }

    @Test
    void getDriverRideHistory_ShouldReturnAllRides() {
        List<ReservationMst> rideHistory = createMockRideHistory();
        when(reservationMstRepository.getRideHistory(any(Date.class), eq(DRIVER_ID)))
                .thenReturn(rideHistory);

        ResponseEntity<Object> response = driverManagementService.getDriverRideHistory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        List<ReservationMst> rides = (List<ReservationMst>) response.getBody();
        assertEquals(2, rides.size());
    }

    private UserMst createTestDriver() {
        UserMst driver = new UserMst();
        driver.setId(DRIVER_ID);
        driver.setUsername(DRIVER_USERNAME);
        driver.setFirstName("Test");
        driver.setLastName("Driver");
        driver.setIsOnline(STATUS_NO);
        driver.setOnTrip(false);
        driver.setStatus('A');
        driver.setRoleId(ROLE_ID_DRIVER);
        return driver;
    }

    private void setupSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testDriver, null)
        );
    }

    private List<TransactionMst> createMockTransactions() {
        List<TransactionMst> transactions = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        
        // Today's transaction
        TransactionMst today = new TransactionMst();
        today.setCreatedOn(cal.getTime());
        transactions.add(today);

        // Yesterday's transaction
        cal.add(Calendar.DAY_OF_MONTH, -1);
        TransactionMst yesterday = new TransactionMst();
        yesterday.setCreatedOn(cal.getTime());
        transactions.add(yesterday);

        return transactions;
    }

    private List<ReservationMst> createMockUpcomingReservations() {
        List<ReservationMst> reservations = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);

        for (int i = 0; i < 2; i++) {
            ReservationMst reservation = new ReservationMst();
            reservation.setPickUpDate(cal.getTime());
            reservation.setStatus(STATUS_ACTIVE);
            reservations.add(reservation);
            cal.add(Calendar.HOUR, 1);
        }

        return reservations;
    }

    private List<ReservationMst> createMockRideHistory() {
        List<ReservationMst> reservations = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        for (int i = 0; i < 2; i++) {
            ReservationMst reservation = new ReservationMst();
            reservation.setPickUpDate(cal.getTime());
            reservation.setStatus(STATUS_COMPLETE);
            reservations.add(reservation);
            cal.add(Calendar.HOUR, -1);
        }

        return reservations;
    }
}
