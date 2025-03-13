package com.bms.service;

public interface EmailService {

    void sendEmails();

    void sendEmailWithAttachment(Integer id, byte[] bytes);
}
