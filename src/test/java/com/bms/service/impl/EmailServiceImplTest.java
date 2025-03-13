package com.bms.service.impl;

import com.bms.entity.CommonEmailMst;
import com.bms.repository.CommonEmailMstRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import jakarta.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.bms.util.CommonConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("test")
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private CommonEmailMstRepository commonEmailMstRepository;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private static final String TEST_FROM_EMAIL = "test@example.com";
    private static final String TEST_TO_EMAIL = "user@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_CONTENT = "Test Content";
    private static final Integer MAX_RETRY_COUNT = 3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailService, "fromMail", TEST_FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "maxRetryCount", MAX_RETRY_COUNT);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendEmails_WithUnsentEmails_ShouldSendSuccessfully() {
        // Arrange
        List<CommonEmailMst> unsentEmails = Arrays.asList(
            createTestEmail(1, STATUS_UNSENT),
            createTestEmail(2, STATUS_UNSENT)
        );
        
        when(commonEmailMstRepository.getAllUnsentEmails()).thenReturn(unsentEmails);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendEmails();

        // Assert
        verify(commonEmailMstRepository).saveAll(argThat(emails -> {
            List<CommonEmailMst> emailList = (List<CommonEmailMst>) emails;
            return emailList.stream().allMatch(email -> 
                email.getStatus() == STATUS_SENT &&
                SYSTEM_SCHEDULER.equals(email.getUpdateBy()) &&
                email.getUpdateOn() != null
            );
        }));
        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    void sendEmails_WhenSendingFails_ShouldIncrementRetryCount() {
        // Arrange
        CommonEmailMst email = createTestEmail(1, STATUS_UNSENT);
        email.setRetryCount(0);
        
        when(commonEmailMstRepository.getAllUnsentEmails()).thenReturn(Arrays.asList(email));
        doThrow(new MailSendException("Send failed")).when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendEmails();

        // Assert
        verify(commonEmailMstRepository).saveAll(argThat(emails -> {
            CommonEmailMst savedEmail = ((List<CommonEmailMst>) emails).get(0);
            return savedEmail.getRetryCount() == 1 &&
                   savedEmail.getStatus() == STATUS_UNSENT &&
                   SYSTEM_SCHEDULER.equals(savedEmail.getUpdateBy());
        }));
    }

    @Test
    void sendEmails_WhenMaxRetriesExceeded_ShouldMarkAsFailed() {
        // Arrange
        CommonEmailMst email = createTestEmail(1, STATUS_UNSENT);
        email.setRetryCount(MAX_RETRY_COUNT);
        
        when(commonEmailMstRepository.getAllUnsentEmails()).thenReturn(Arrays.asList(email));
        doThrow(new MailSendException("Send failed")).when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendEmails();

        // Assert
        verify(commonEmailMstRepository).saveAll(argThat(emails -> {
            CommonEmailMst savedEmail = ((List<CommonEmailMst>) emails).get(0);
            return savedEmail.getRetryCount() == MAX_RETRY_COUNT + 1 &&
                   savedEmail.getStatus() == STATUS_FAILED;
        }));
    }

    @Test
    void sendEmailWithAttachment_WhenEmailExists_ShouldSendSuccessfully() {
        // Arrange
        CommonEmailMst email = createTestEmail(1, STATUS_UNSENT);
        byte[] attachment = "test content".getBytes();
        
        when(commonEmailMstRepository.findById(1)).thenReturn(Optional.of(email));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendEmailWithAttachment(1, attachment);

        // Assert
        verify(commonEmailMstRepository).save(argThat(savedEmail -> {
            CommonEmailMst emailMst = (CommonEmailMst) savedEmail;
            return emailMst.getStatus() == STATUS_SENT &&
                   SYSTEM_SCHEDULER.equals(emailMst.getUpdateBy()) &&
                   emailMst.getUpdateOn() != null;
        }));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmailWithAttachment_WhenEmailNotFound_ShouldNotSend() {
        // Arrange
        when(commonEmailMstRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        emailService.sendEmailWithAttachment(1, "test".getBytes());

        // Assert
        verify(mailSender, never()).send(any(MimeMessage.class));
        verify(commonEmailMstRepository, never()).save(any());
    }

    @Test
    void sendEmailWithAttachment_WhenSendingFails_ShouldIncrementRetryCount() {
        // Arrange
        CommonEmailMst email = createTestEmail(1, STATUS_UNSENT);
        email.setRetryCount(0);
        byte[] attachment = "test content".getBytes();
        
        when(commonEmailMstRepository.findById(1)).thenReturn(Optional.of(email));
        doThrow(new MailSendException("Send failed")).when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendEmailWithAttachment(1, attachment);

        // Assert
        verify(commonEmailMstRepository).save(argThat(savedEmail -> {
            CommonEmailMst emailMst = (CommonEmailMst) savedEmail;
            return emailMst.getRetryCount() == 1 &&
                   emailMst.getStatus() == STATUS_UNSENT &&
                   SYSTEM_SCHEDULER.equals(emailMst.getUpdateBy());
        }));
    }

    private CommonEmailMst createTestEmail(Integer id, Character status) {
        CommonEmailMst email = new CommonEmailMst();
        email.setId(id);
        email.setSendTo(TEST_TO_EMAIL);
        email.setSubject(TEST_SUBJECT);
        email.setContent(TEST_CONTENT);
        email.setStatus(status);
        email.setRetryCount(0);
        email.setCreatedOn(new Date());
        return email;
    }
}
