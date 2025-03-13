package com.bms.service.impl;

import com.bms.dto.AddressDto;
import com.bms.dto.UserDto;
import com.bms.entity.AddressMst;
import com.bms.entity.UserMst;
import com.bms.exception.BusinessException;
import com.bms.repository.AddressMstRepository;
import com.bms.repository.CommonEmailTemplateRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

    @Mock
    private UserMstRepository userMstRepository;

    @Mock
    private CommonEmailTemplateRepository commonEmailTemplateRepository;

    @Mock
    private AddressMstRepository addressMstRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementServiceImpl userService;

    private UserDto testUserDto;
    private UserMst testUser;
    private AddressDto testAddressDto;
    private AddressMst testAddress;

    @BeforeEach
    void setUp() {
        testUser = new UserMst();
        testUser.setId(1);
        testUser.setUsername("johndoe");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setStatus(STATUS_ACTIVE);
        testUser.setRoleId(ROLE_ID_CUSTOMER);

        testUserDto = new UserDto();
        testUserDto.setId(1);
        testUserDto.setUsername("johndoe");
        testUserDto.setPassword("password123");
        testUserDto.setFirstName("John");
        testUserDto.setLastName("Doe");
        testUserDto.setEmail("john.doe@example.com");
        testUserDto.setRoleId(ROLE_ID_CUSTOMER);

        testAddress = AddressMst.builder()
            .id(1)
            .userId(testUser.getId())
            .addressLine1("123 Main St")
            .addressLine2(null)
            .city("New York")
            .state("NY")
            .country("USA")
            .postalCode("10001")
            .build();

        testAddressDto = new AddressDto();
        testAddressDto.setAddressLine1("123 Main St");
        testAddressDto.setAddressLine2(null);
        testAddressDto.setCity("New York");
        testAddressDto.setState("NY");
        testAddressDto.setCountry("USA");
        testAddressDto.setPostalCode("10001");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void confirmUserEmail_AlreadyConfirmed() throws BusinessException {
        // Arrange
        when(userMstRepository.findUserByUuid("test-uuid")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Object> response = userService.confirmUserEmail("test-uuid");

        // Assert
        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());
        verify(userMstRepository, never()).save(any());
    }

    @Test
    void getUserAddress() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        when(addressMstRepository.getAddressMstByUserName(testUser.getUsername()))
            .thenReturn(testAddress);

        // Act
        ResponseEntity<Object> response = userService.getUserAddress();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAddress, response.getBody());
    }

    @Test
    void updateUserAddress() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        when(addressMstRepository.getAddressMstByUserName(testUser.getUsername()))
            .thenReturn(testAddress);

        testAddressDto.setAddressLine1("456 New St");

        // Act
        ResponseEntity<Object> response = userService.updateUserAddress(testAddressDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ArgumentCaptor<AddressMst> addressCaptor = ArgumentCaptor.forClass(AddressMst.class);
        verify(addressMstRepository).save(addressCaptor.capture());
        assertEquals("456 New St", addressCaptor.getValue().getAddressLine1());
        assertEquals(testAddressDto.getCity(), addressCaptor.getValue().getCity());
        assertEquals(testAddressDto.getState(), addressCaptor.getValue().getState());
        assertEquals(testAddressDto.getCountry(), addressCaptor.getValue().getCountry());
        assertEquals(testAddressDto.getPostalCode(), addressCaptor.getValue().getPostalCode());
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<UserMst> users = Arrays.asList(testUser);
        when(userMstRepository.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<Object> response = userService.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    void getAllDrivers_Success() {
        // Arrange
        UserMst driver1 = new UserMst();
        driver1.setRoleId(ROLE_ID_DRIVER);
        driver1.setStatus(STATUS_ACTIVE);
        driver1.setUsername("driver1");

        UserMst driver2 = new UserMst();
        driver2.setRoleId(ROLE_ID_DRIVER);
        driver2.setStatus(STATUS_ACTIVE);
        driver2.setUsername("driver2");

        when(userMstRepository.getAllDrivers())
            .thenReturn(Arrays.asList(driver1, driver2));

        // Act
        ResponseEntity<Object> response = userService.getAllDrivers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<UserMst> drivers = (List<UserMst>) response.getBody();
        assertEquals(2, drivers.size());
        assertTrue(drivers.stream().allMatch(d -> 
            d.getRoleId().equals(ROLE_ID_DRIVER) && 
            d.getStatus() != STATUS_DELETE
        ));
    }

    @Test
    void getAllAdmins_Success() {
        // Arrange
        UserMst admin1 = new UserMst();
        admin1.setRoleId(ROLE_ID_ADMIN);
        admin1.setStatus(STATUS_ACTIVE);
        admin1.setUsername("admin1");

        UserMst admin2 = new UserMst();
        admin2.setRoleId(ROLE_ID_ADMIN);
        admin2.setStatus(STATUS_INACTIVE);
        admin2.setUsername("admin2");

        when(userMstRepository.getAllAdmins())
            .thenReturn(Arrays.asList(admin1, admin2));

        // Act
        ResponseEntity<Object> response = userService.getAllAdmins();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<UserMst> admins = (List<UserMst>) response.getBody();
        assertEquals(2, admins.size());
        assertTrue(admins.stream().allMatch(a -> 
            a.getRoleId().equals(ROLE_ID_ADMIN) && 
            a.getStatus() != STATUS_DELETE
        ));
    }

    @Test
    void changeUserStatus_Success() throws BusinessException {
        // Arrange
        UserMst adminUser = new UserMst();
        adminUser.setUsername("admin");
        adminUser.setRoleId(ROLE_ID_ADMIN);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(adminUser, null)
        );

        when(userMstRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Object> response = userService.changeUserStatus(1, STATUS_INACTIVE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ArgumentCaptor<UserMst> userCaptor = ArgumentCaptor.forClass(UserMst.class);
        verify(userMstRepository).save(userCaptor.capture());
        assertEquals(STATUS_INACTIVE, userCaptor.getValue().getStatus());
    }

    @Test
    void getUserDetails_Success() {
        // Arrange
        when(userMstRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Object> response = userService.getUserDetails(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void getUserDetails_UserNotFound() {
        // Arrange
        when(userMstRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> userService.getUserDetails(1));
        assertEquals(USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void loadUserByUsername_Success() {
        // Arrange
        when(userMstRepository.findByUsername(testUser.getUsername()))
            .thenReturn(Optional.of(testUser));

        // Act
        org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername(testUser.getUsername());

        // Assert
        assertEquals(testUser.getUsername(), userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        when(userMstRepository.findByUsername(any()))
            .thenReturn(Optional.empty());

        // Act & Assert
        org.springframework.security.core.AuthenticationException exception = assertThrows(org.springframework.security.core.AuthenticationException.class,
            () -> userService.loadUserByUsername("nonexistent"));
        assertEquals(USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void getLoggedInUserDetails_Success() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        when(userMstRepository.findById(testUser.getId()))
            .thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Object> response = userService.getLoggedInUserDetails();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto returnedUser = (UserDto) response.getBody();
        assertEquals(testUser.getUsername(), returnedUser.getUsername());
        assertEquals(testUser.getEmail(), returnedUser.getEmail());
    }

    @Test
    void getLoggedInUserDetails_UserNotFound() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        when(userMstRepository.findById(testUser.getId()))
            .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.getLoggedInUserDetails());
        assertEquals(USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void updateUser_InvalidUserId() {
        // Arrange
        testUserDto.setId(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> userService.updateUser(testUserDto));
        assertEquals(USER_ID_CANNOT_BE_EMPTY, exception.getMessage());
        verify(userMstRepository, never()).save(any());
    }

    @Test
    void updateUserAddress_UpdateMetadata() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );
        when(addressMstRepository.getAddressMstByUserName(testUser.getUsername()))
            .thenReturn(testAddress);

        // Act
        ResponseEntity<Object> response = userService.updateUserAddress(testAddressDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ArgumentCaptor<AddressMst> addressCaptor = ArgumentCaptor.forClass(AddressMst.class);
        verify(addressMstRepository).save(addressCaptor.capture());
        
        // Verify metadata is properly set
        AddressMst savedAddress = addressCaptor.getValue();
        assertEquals(testUser.getId(), savedAddress.getUserId());
        assertEquals(testAddressDto.getAddressLine1(), savedAddress.getAddressLine1());
        assertEquals(testAddressDto.getCity(), savedAddress.getCity());
        assertEquals(testAddressDto.getState(), savedAddress.getState());
        assertEquals(testAddressDto.getCountry(), savedAddress.getCountry());
        assertEquals(testAddressDto.getPostalCode(), savedAddress.getPostalCode());
    }

    @Test
    void updateUser_NoPasswordChange() throws BusinessException, IOException {
        // Arrange
        testUserDto.setPassword("");  // Empty password means no change
        when(userMstRepository.findById(1)).thenReturn(Optional.of(testUser));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );

        // Act
        ResponseEntity<Object> response = userService.updateUser(testUserDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ArgumentCaptor<UserMst> userCaptor = ArgumentCaptor.forClass(UserMst.class);
        verify(userMstRepository).save(userCaptor.capture());
        UserMst updatedUser = userCaptor.getValue();
        
        // Verify password wasn't changed
        assertEquals(testUser.getPassword(), updatedUser.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateUser_WithDriverLicense_StorageError() throws BusinessException, IOException {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null)
        );

        String errorMessage = "Error deleting file: test.jpg";
        when(userMstRepository.findById(testUserDto.getId())).thenReturn(Optional.of(testUser));
        when(fileStorageService.uploadDriverLicense(any(), any(), any()))
            .thenThrow(new BusinessException(errorMessage, HttpStatus.EXPECTATION_FAILED));

        testUserDto.setDriverLicenseNo("test-license");
        testUserDto.setDrivingLicense(new MockMultipartFile(
            "driverLicense",
            "test.jpg",
            "image/jpeg",
            "test content".getBytes()
        ));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
            () -> userService.updateUser(testUserDto));
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(HttpStatus.EXPECTATION_FAILED, exception.getStatus());
        verify(userMstRepository, never()).save(any());
    }
}
