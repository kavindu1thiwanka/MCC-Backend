package com.bms.service.impl;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.VehicleMstDto;
import com.bms.entity.ReservationMst;
import com.bms.entity.UserMst;
import com.bms.entity.VehicleMst;
import com.bms.exception.BusinessException;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.VehicleManagementCustomRepository;
import com.bms.repository.VehicleMstRepository;
import com.bms.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
@ExtendWith(MockitoExtension.class)
class VehicleManagementServiceImplTest {

    @Mock
    private VehicleManagementCustomRepository vehicleManagementCustomRepository;

    @Mock
    private ReservationMstRepository reservationMstRepository;

    @Mock
    private VehicleMstRepository vehicleMstRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private VehicleManagementServiceImpl vehicleService;

    private VehicleMstDto testVehicleDto;
    private VehicleMst testVehicle;
    private UserMst testUser;
    private CommonFilterDto testFilterDto;
    private ReservationDto testReservationDto;

    @BeforeEach
    void setUp() {
        testVehicle = new VehicleMst();
        testVehicle.setVehicleNo("ZXC123");
        testVehicle.setVehicleModel("Toyota Camry");
        testVehicle.setVehicleType("Sedan");
        testVehicle.setCategory("Luxury");
        testVehicle.setSeats(4);
        testVehicle.setGearType('A');
        testVehicle.setPricePerDay(BigDecimal.valueOf(5000));
        testVehicle.setStatus(STATUS_ACTIVE);

        testVehicleDto = new VehicleMstDto();
        testVehicleDto.setVehicleNo("ZXC123");
        testVehicleDto.setName("Toyota Camry");
        testVehicleDto.setVehicleType("Sedan");
        testVehicleDto.setCategory("Luxury");
        testVehicleDto.setSeats(4);
        testVehicleDto.setGearType('A');
        testVehicleDto.setPricePerDay(BigDecimal.valueOf(5000));

        testUser = new UserMst();
        testUser.setUsername("admin");
        testUser.setRoleId(ROLE_ID_ADMIN);

        testFilterDto = new CommonFilterDto();
        testFilterDto.setCategory("Luxury");
        testFilterDto.setPickUpDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        testFilterDto.setReturnDate(cal.getTime());

        testReservationDto = new ReservationDto();
        testReservationDto.setPickUpDate(new Date());
        testReservationDto.setReturnDate(cal.getTime());
        testReservationDto.setPricePerDay(BigDecimal.valueOf(5000));
        testReservationDto.setNeedDriver(true);
    }

    @Test
    void getVehicleList_Success() {
        // Arrange
        List<VehicleMstDto> vehicles = Arrays.asList(testVehicleDto);
        when(vehicleManagementCustomRepository.getVehicleList(any(CommonFilterDto.class)))
            .thenReturn(vehicles);
        when(reservationMstRepository.getAlreadyBookedVehicles(anyString()))
            .thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<Object> response = vehicleService.getVehicleList(testFilterDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<VehicleMstDto> result = (List<VehicleMstDto>) response.getBody();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVehicleDto.getVehicleNo(), result.get(0).getVehicleNo());
    }

    @Test
    void addVehicle_Success() throws BusinessException, IOException {
        // Arrange
        MockMultipartFile vehicleImage = new MockMultipartFile(
            "vehicleImage", "car.jpg", "image/jpeg", "test".getBytes()
        );
        when(vehicleMstRepository.existsByVehicleNo(anyString())).thenReturn(false);
        when(fileStorageService.uploadVehicleImage(any(), anyString(), any()))
            .thenReturn("vehicle-image-url");

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );

        // Act
        ResponseEntity<Object> response = vehicleService.addVehicle(testVehicleDto, vehicleImage);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        ArgumentCaptor<VehicleMst> vehicleCaptor = ArgumentCaptor.forClass(VehicleMst.class);
        verify(vehicleMstRepository).save(vehicleCaptor.capture());
        VehicleMst savedVehicle = vehicleCaptor.getValue();
        assertEquals(testVehicleDto.getVehicleNo(), savedVehicle.getVehicleNo());
        assertEquals(testVehicleDto.getName(), savedVehicle.getVehicleModel());
        assertEquals("vehicle-image-url", savedVehicle.getVehicleImage());
        assertEquals(STATUS_ACTIVE, savedVehicle.getStatus());
    }

    @Test
    void addVehicle_ValidationFailures() {
        // Test null vehicle details
        BusinessException exception = assertThrows(BusinessException.class,
            () -> vehicleService.addVehicle(null, new MockMultipartFile("ABC", "ABC.jpg", "", "".getBytes())));
        assertEquals(VEHICLE_DETAILS_CANNOT_BE_NULL, exception.getMessage());

        // Test empty vehicle number
        testVehicleDto.setVehicleNo("");
        exception = assertThrows(BusinessException.class,
            () -> vehicleService.addVehicle(testVehicleDto, new MockMultipartFile("ABC", "ABC.jpg", "", "".getBytes())));
        assertEquals(VEHICLE_NO_CANNOT_BE_EMPTY, exception.getMessage());

        // Test duplicate vehicle
        testVehicleDto.setVehicleNo("ABC123");
        when(vehicleMstRepository.existsByVehicleNo(anyString())).thenReturn(true);
        exception = assertThrows(BusinessException.class,
            () -> vehicleService.addVehicle(testVehicleDto, new MockMultipartFile("ABC", "ABC.jpg", "", "".getBytes())));
        assertEquals(VEHICLE_ALREADY_EXISTS, exception.getMessage());

        verify(vehicleMstRepository, never()).save(any());
    }

    @Test
    void getVehicleTotalCost_Success() {
        // Act
        ResponseEntity<Object> response = vehicleService.getVehicleTotalCost(testReservationDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) response.getBody();
        assertNotNull(result);
        BigDecimal totalCost = (BigDecimal) result.get("totalCost");
        // 3 days * 5000 per day + (3 days * 1200 for driver)
        assertEquals(BigDecimal.valueOf(18600), totalCost);
    }

    @Test
    void updateVehicle_NotFound() {
        // Arrange
        when(vehicleMstRepository.findByVehicleNo(anyString()))
            .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> vehicleService.updateVehicle(testVehicleDto, null));
        assertEquals(VEHICLE_NOT_FOUND, exception.getMessage());
        verify(vehicleMstRepository, never()).save(any());
    }

    @Test
    void updateVehicleStatus_Success() throws BusinessException {
        // Arrange
        when(vehicleMstRepository.findByVehicleNo(anyString()))
            .thenReturn(Optional.of(testVehicle));

        // Act
        ResponseEntity<Object> response = vehicleService.updateVehicleStatus("ABC123", STATUS_INACTIVE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ArgumentCaptor<VehicleMst> vehicleCaptor = ArgumentCaptor.forClass(VehicleMst.class);
        verify(vehicleMstRepository).save(vehicleCaptor.capture());
        assertEquals(STATUS_INACTIVE, vehicleCaptor.getValue().getStatus());
    }

    @Test
    void getVehicleDetails_Success() throws BusinessException {
        // Arrange
        when(vehicleMstRepository.findByVehicleNo(anyString()))
            .thenReturn(Optional.of(testVehicle));

        // Act
        ResponseEntity<Object> response = vehicleService.getVehicleDetails("ABC123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testVehicle, response.getBody());
    }

    @Test
    void getVehicleDetails_NotFound() {
        // Arrange
        when(vehicleMstRepository.findByVehicleNo(anyString()))
            .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> vehicleService.getVehicleDetails("ABC123"));
        assertEquals(VEHICLE_NOT_FOUND, exception.getMessage());
    }
}
