package com.bms.service.impl;

import com.bms.entity.CommonEmailMst;
import com.bms.repository.CommonEmailMstRepository;
import com.bms.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bms.util.CommonConstants.*;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;
    private CommonEmailMstRepository commonEmailMstRepository;

    @Value(MAX_RETRY_COUNT)
    private Integer maxRetryCount;
    @Value(FROM_MAIL)
    private String fromMail;

    private final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendEmails() {
        List<CommonEmailMst> unsentEmailsList = commonEmailMstRepository.getAllUnsentEmails();

        for (CommonEmailMst emailMst : unsentEmailsList) {

            emailMst.setUpdateOn(new Date());
            emailMst.setUpdateBy(SYSTEM_SCHEDULER);

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setFrom(fromMail);
                helper.setTo(emailMst.getSendTo());
                helper.setSubject(emailMst.getSubject());
                helper.setText(emailMst.getContent(), true);
                mailSender.send(message);

                emailMst.setStatus(STATUS_SENT);
            } catch (Exception e) {
                emailMst.setRetryCount(emailMst.getRetryCount() + 1);
                emailMst.setStatus(maxRetryCount <= emailMst.getRetryCount() ? STATUS_FAILED : STATUS_UNSENT);
                LOGGER.error("Error while sending email", e);
            }
        }

        commonEmailMstRepository.saveAll(unsentEmailsList);
    }

    @Override
    public void sendEmailWithAttachment(Integer emailMstId, byte[] bytes) {
        CommonEmailMst emailMst = commonEmailMstRepository.findById(emailMstId).orElse(null);

        if (emailMst == null) {
            LOGGER.error("Email with ID {} not found.", emailMstId);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromMail);
            helper.setTo(emailMst.getSendTo());
            helper.setSubject(emailMst.getSubject());
            helper.setText(emailMst.getContent(), true);

            // Attach the file
            helper.addAttachment("Invoice.pdf", new ByteArrayDataSource(bytes, "application/pdf"));

            mailSender.send(message);

            emailMst.setStatus(STATUS_SENT);
        } catch (Exception e) {
            emailMst.setRetryCount(emailMst.getRetryCount() + 1);
            emailMst.setStatus(maxRetryCount <= emailMst.getRetryCount() ? STATUS_FAILED : STATUS_UNSENT);
            LOGGER.error("Error while sending email with attachment", e);
        }

        emailMst.setUpdateOn(new Date());
        emailMst.setUpdateBy(SYSTEM_SCHEDULER);
        commonEmailMstRepository.save(emailMst);
    }


    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void setCommonEmailMstRepository(CommonEmailMstRepository commonEmailMstRepository) {
        this.commonEmailMstRepository = commonEmailMstRepository;
    }
}
