package com.bms.controller;

import com.bms.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
public class EmailSendingController {

    private EmailServiceImpl emailService;

    /**
     * This method is used to send emails
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 1000)
    private void sendEmails() {
        emailService.sendEmails();
    }

    @Autowired
    public void setEmailService(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }
}
