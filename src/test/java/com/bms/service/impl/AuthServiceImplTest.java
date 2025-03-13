package com.bms.service.impl;

import com.bms.config.JwtUtil;
import com.bms.dto.AuthRequestDto;
import com.bms.entity.CommonEmailTemplate;
import com.bms.entity.UserMst;
import com.bms.exception.BusinessException;
import com.bms.repository.CommonEmailMstRepository;
import com.bms.repository.CommonEmailTemplateRepository;
import com.bms.repository.UserMstRepository;
import com.bms.util.ExceptionMessages;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.EMAIL_TEMPLATE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMstRepository userMstRepository;

    @Mock
    private CommonEmailTemplateRepository commonEmailTemplateRepository;

    @Mock
    private CommonEmailMstRepository commonEmailMstRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpass";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "test-token";
    private static final String TEST_REFRESH_TOKEN = "test-refresh-token";
    private static final String PWD_RESET_URL_VALUE = "http://localhost:4200/auth/reset-password?token={token}";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authService, "pwdResetUrl", PWD_RESET_URL_VALUE);
        // Set up default security context
        Authentication auth = new UsernamePasswordAuthenticationToken("system", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnTokens() {
        // Arrange
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        UserMst user = createTestUser();
        Authentication authentication = mock(Authentication.class);

        when(userMstRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        when(jwtUtil.generateAccessToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);
        when(jwtUtil.generateRefreshToken(TEST_USERNAME)).thenReturn(TEST_REFRESH_TOKEN);

        // Act
        ResponseEntity<Object> response = authService.authenticateUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthRequestDto responseDto = (AuthRequestDto) response.getBody();
        assertNotNull(responseDto);
        assertEquals(TEST_TOKEN, responseDto.getAccessToken());
        assertEquals(TEST_REFRESH_TOKEN, responseDto.getRefreshToken());
    }

    @Test
    void authenticateUser_WithInvalidUsername_ShouldReturnUnauthorized() {
        AuthRequestDto request = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        
        when(userMstRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = authService.authenticateUser(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ExceptionMessages.USER_NOT_FOUND, response.getBody());
    }

    @Test
    void authenticateUser_WithInvalidPassword_ShouldReturnUnauthorized() {
        AuthRequestDto request = new AuthRequestDto(TEST_USERNAME, TEST_PASSWORD);
        UserMst user = createTestUser();
        
        when(userMstRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(false);

        ResponseEntity<Object> response = authService.authenticateUser(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ExceptionMessages.USER_NOT_FOUND, response.getBody());
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewAccessToken() {
        AuthRequestDto request = new AuthRequestDto();
        request.setRefreshToken(TEST_REFRESH_TOKEN);
        
        when(jwtUtil.extractUsername(TEST_REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtUtil.validateToken(TEST_REFRESH_TOKEN, TEST_USERNAME)).thenReturn(true);
        when(jwtUtil.generateAccessToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);

        ResponseEntity<Object> response = authService.refreshToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthRequestDto responseDto = (AuthRequestDto) response.getBody();
        assertNotNull(responseDto);
        assertEquals(TEST_TOKEN, responseDto.getAccessToken());
        assertEquals(TEST_REFRESH_TOKEN, responseDto.getRefreshToken());
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldReturnUnauthorized() {
        AuthRequestDto request = new AuthRequestDto();
        request.setRefreshToken(TEST_REFRESH_TOKEN);
        
        when(jwtUtil.extractUsername(TEST_REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtUtil.validateToken(TEST_REFRESH_TOKEN, TEST_USERNAME)).thenReturn(false);

        ResponseEntity<Object> response = authService.refreshToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid refresh token.", response.getBody());
    }

    @Test
    void sendPasswordResetMail_WithValidEmail_ShouldSendEmail() throws BusinessException {
        UserMst user = createTestUser();
        CommonEmailTemplate template = createEmailTemplate();
        
        when(userMstRepository.findByEmailAndStatusNot(TEST_EMAIL, STATUS_DELETE))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generatePasswordResetToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);
        when(commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_PWD_RESET))
                .thenReturn(Optional.of(template));

        ResponseEntity<Object> response = authService.sendPasswordResetMail(TEST_EMAIL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commonEmailMstRepository).save(argThat(email -> {
            assertEquals(TEST_EMAIL, email.getSendTo());
            assertNotNull(email.getCreatedBy());
            assertEquals("system", email.getCreatedBy());
            return true;
        }));
    }

    @Test
    void sendPasswordResetMail_WithInvalidEmail_ShouldThrowException() {
        when(userMstRepository.findByEmailAndStatusNot(TEST_EMAIL, STATUS_DELETE))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.sendPasswordResetMail(TEST_EMAIL));

        assertEquals(ExceptionMessages.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void sendPasswordResetMail_WithMissingTemplate_ShouldThrowException() {
        UserMst user = createTestUser();
        
        when(userMstRepository.findByEmailAndStatusNot(TEST_EMAIL, STATUS_DELETE))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generatePasswordResetToken(TEST_USERNAME)).thenReturn(TEST_TOKEN);
        when(commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_PWD_RESET))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.sendPasswordResetMail(TEST_EMAIL));

        assertEquals(EMAIL_TEMPLATE_NOT_FOUND, exception.getMessage());
    }

    private UserMst createTestUser() {
        UserMst user = new UserMst();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setEmail(TEST_EMAIL);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus('A');
        user.setRoleId(2);
        return user;
    }

    private CommonEmailTemplate createEmailTemplate() {
        CommonEmailTemplate template = new CommonEmailTemplate();
        template.setId(EMAIL_TEMPLATE_PWD_RESET);
        template.setSubject("Password Reset");
        template.setTemplateData("""
                <html>
                <body>
                    <div id="EMAIL_SEND_TO"></div>
                    <a id="PWD_RESET_URL" href="">Reset Password</a>
                </body>
                </html>
                """);
        return template;
    }
}
