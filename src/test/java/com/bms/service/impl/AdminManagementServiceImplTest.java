package com.bms.service.impl;

import com.bms.entity.ReservationMst;
import com.bms.entity.UserMst;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.UserMstRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;


import static com.bms.util.CommonConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
class AdminManagementServiceImplTest {

    @Mock
    private UserMstRepository userMstRepository;

    @Mock
    private ReservationMstRepository reservationMstRepository;

    @InjectMocks
    private AdminManagementServiceImpl adminManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadDashboardDetails_ShouldReturnCompleteStats() {

        when(userMstRepository.getAllActiveUsersCount()).thenReturn(10);
        
        List<UserMst> mockDrivers = createMockDrivers();
        when(userMstRepository.getAllActiveDrivers()).thenReturn(mockDrivers);

        List<ReservationMst> mockReservations = createMockReservations();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        when(reservationMstRepository.getReservationDetailsAfterDate(any(Date.class)))
                .thenReturn(mockReservations);

        ResponseEntity<Object> response = adminManagementService.loadDashboardDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        
        // Verify stats
        @SuppressWarnings("unchecked")
        HashMap<String, Object> stats = (HashMap<String, Object>) responseBody.get("stats");
        assertEquals(10, stats.get("totalUsers"));
        assertEquals(3, stats.get("totalDrivers"));
        assertEquals(1, stats.get("activeRides"));

        // Verify pie chart data
        Integer[] pieChartData = (Integer[]) responseBody.get("pieChartData");
        assertEquals(1, pieChartData[0]);
        assertEquals(2, pieChartData[1]);
        assertEquals(1, pieChartData[2]);

        // Verify line chart data
        @SuppressWarnings("unchecked")
        HashMap<String, Object> lineChartData = (HashMap<String, Object>) responseBody.get("lineChartData");
        assertNotNull(lineChartData.get("labels"));
        assertNotNull(lineChartData.get("datasets"));

        // Verify repository calls
        verify(userMstRepository).getAllActiveUsersCount();
        verify(userMstRepository).getAllActiveDrivers();
        verify(reservationMstRepository).getReservationDetailsAfterDate(any(Date.class));
    }

    @Test
    void loadDashboardDetails_WithNoData_ShouldReturnEmptyStats() {
        when(userMstRepository.getAllActiveUsersCount()).thenReturn(0);
        when(userMstRepository.getAllActiveDrivers()).thenReturn(new ArrayList<>());
        when(reservationMstRepository.getReservationDetailsAfterDate(any(Date.class)))
                .thenReturn(new ArrayList<>());

        ResponseEntity<Object> response = adminManagementService.loadDashboardDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> stats = (HashMap<String, Object>) responseBody.get("stats");
        assertEquals(0, stats.get("totalUsers"));
        assertEquals(0, stats.get("totalDrivers"));
        assertEquals(0, stats.get("activeRides"));

        Integer[] pieChartData = (Integer[]) responseBody.get("pieChartData");
        assertArrayEquals(new Integer[]{0, 0, 0}, pieChartData);

        @SuppressWarnings("unchecked")
        HashMap<String, Object> lineChartData = (HashMap<String, Object>) responseBody.get("lineChartData");
        assertNotNull(lineChartData.get("labels"));
        assertTrue(((List<?>) lineChartData.get("labels")).isEmpty());
    }

    @Test
    void loadDashboardDetails_WithAllDriversOnline_ShouldShowCorrectStats() {
        when(userMstRepository.getAllActiveUsersCount()).thenReturn(5);
        
        List<UserMst> allOnlineDrivers = createAllOnlineDrivers();
        when(userMstRepository.getAllActiveDrivers()).thenReturn(allOnlineDrivers);
        when(reservationMstRepository.getReservationDetailsAfterDate(any(Date.class)))
                .thenReturn(new ArrayList<>());

        ResponseEntity<Object> response = adminManagementService.loadDashboardDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> stats = (HashMap<String, Object>) responseBody.get("stats");
        assertEquals(5, stats.get("totalUsers"));
        assertEquals(3, stats.get("totalDrivers"));

        Integer[] pieChartData = (Integer[]) responseBody.get("pieChartData");
        assertArrayEquals(new Integer[]{3, 0, 0}, pieChartData);
    }

    @Test
    void loadDashboardDetails_WithAllDriversOnTrip_ShouldShowCorrectStats() {
        when(userMstRepository.getAllActiveUsersCount()).thenReturn(8);
        
        List<UserMst> allOnTripDrivers = createAllOnTripDrivers();
        when(userMstRepository.getAllActiveDrivers()).thenReturn(allOnTripDrivers);
        
        List<ReservationMst> activeReservations = createActiveReservations();
        when(reservationMstRepository.getReservationDetailsAfterDate(any(Date.class)))
                .thenReturn(activeReservations);

        ResponseEntity<Object> response = adminManagementService.loadDashboardDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> stats = (HashMap<String, Object>) responseBody.get("stats");
        assertEquals(8, stats.get("totalUsers"));
        assertEquals(3, stats.get("totalDrivers"));
        assertEquals(3, stats.get("activeRides"));

        Integer[] pieChartData = (Integer[]) responseBody.get("pieChartData");
        assertArrayEquals(new Integer[]{0, 3, 3}, pieChartData);
    }

    @Test
    void loadDashboardDetails_WithMultipleSameDayReservations_ShouldAggregateCorrectly() {
        when(userMstRepository.getAllActiveUsersCount()).thenReturn(10);
        when(userMstRepository.getAllActiveDrivers()).thenReturn(new ArrayList<>());
        
        List<ReservationMst> multipleReservations = createMultipleSameDayReservations();
        when(reservationMstRepository.getReservationDetailsAfterDate(any(Date.class)))
                .thenReturn(multipleReservations);

        ResponseEntity<Object> response = adminManagementService.loadDashboardDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> responseBody = (HashMap<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        HashMap<String, Object> lineChartData = (HashMap<String, Object>) responseBody.get("lineChartData");
        
        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) lineChartData.get("labels");
        assertEquals(1, labels.size(), "Should have only one date label for same-day reservations");
        
        @SuppressWarnings("unchecked")
        List<Integer[]> datasets = (List<Integer[]>) lineChartData.get("datasets");
        Integer[] completedData = datasets.get(0);
        Integer[] cancelledData = datasets.get(1);
        
        assertEquals(2, completedData[0], "Should have 2 completed reservations");
        assertEquals(3, cancelledData[0], "Should have 3 cancelled reservations");
    }

    private List<UserMst> createMockDrivers() {
        List<UserMst> drivers = new ArrayList<>();
        
        UserMst onlineDriver = new UserMst();
        onlineDriver.setIsOnline('Y');
        onlineDriver.setOnTrip(false);
        
        UserMst offlineDriver = new UserMst();
        offlineDriver.setIsOnline('N');
        offlineDriver.setOnTrip(false);
        
        UserMst onTripDriver = new UserMst();
        onTripDriver.setIsOnline('N');
        onTripDriver.setOnTrip(true);
        
        drivers.add(onlineDriver);
        drivers.add(offlineDriver);
        drivers.add(onTripDriver);
        
        return drivers;
    }

    private List<UserMst> createAllOnlineDrivers() {
        List<UserMst> drivers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UserMst driver = new UserMst();
            driver.setIsOnline('Y');
            driver.setOnTrip(false);
            drivers.add(driver);
        }
        return drivers;
    }

    private List<UserMst> createAllOnTripDrivers() {
        List<UserMst> drivers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UserMst driver = new UserMst();
            driver.setIsOnline('N');
            driver.setOnTrip(true);
            drivers.add(driver);
        }
        return drivers;
    }

    private List<ReservationMst> createActiveReservations() {
        List<ReservationMst> reservations = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date lastMonth = cal.getTime();
        
        for (int i = 0; i < 3; i++) {
            ReservationMst reservation = new ReservationMst();
            reservation.setStatus(STATUS_ACTIVE);
            reservation.setPickUpDate(lastMonth);
            reservations.add(reservation);
        }
        
        return reservations;
    }

    private List<ReservationMst> createMultipleSameDayReservations() {
        List<ReservationMst> reservations = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date lastMonth = cal.getTime();
        
        // Add 2 completed reservations
        for (int i = 0; i < 2; i++) {
            ReservationMst reservation = new ReservationMst();
            reservation.setStatus(STATUS_COMPLETE);
            reservation.setPickUpDate(lastMonth);
            reservations.add(reservation);
        }
        
        // Add 3 cancelled reservations
        for (int i = 0; i < 3; i++) {
            ReservationMst reservation = new ReservationMst();
            reservation.setStatus(STATUS_RESERVATION_CANCELLED);
            reservation.setPickUpDate(lastMonth);
            reservations.add(reservation);
        }
        
        return reservations;
    }

    private List<ReservationMst> createMockReservations() {
        List<ReservationMst> reservations = new ArrayList<>();
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);  // Set to last month
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date lastMonth = cal.getTime();
        
        // Create completed reservation
        ReservationMst completedReservation = new ReservationMst();
        completedReservation.setStatus(STATUS_COMPLETE);
        completedReservation.setPickUpDate(lastMonth);
        
        // Create cancelled reservation
        ReservationMst cancelledReservation = new ReservationMst();
        cancelledReservation.setStatus(STATUS_RESERVATION_CANCELLED);
        cancelledReservation.setPickUpDate(lastMonth);
        
        // Create active reservation
        ReservationMst activeReservation = new ReservationMst();
        activeReservation.setStatus(STATUS_ACTIVE);
        activeReservation.setPickUpDate(lastMonth);
        
        reservations.add(completedReservation);
        reservations.add(cancelledReservation);
        reservations.add(activeReservation);
        
        return reservations;
    }
}
