package com.bms.service.impl;

import com.bms.entity.CommonEmailMst;
import com.bms.repository.CommonEmailMstRepository;
import com.bms.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Date;
import java.util.List;

import static com.bms.util.CommonConstants.*;

@Service
@RequestScope
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;
    private CommonEmailMstRepository commonEmailMstRepository;

    @Value(MAX_RETRY_COUNT)
    private Integer maxRetryCount;
    @Value(FROM_MAIL)
    private String fromMail;

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
                helper.setTo(emailMst.getTo());
                helper.setSubject(emailMst.getSubject());
                helper.setText(emailMst.getSubject(), true);
                mailSender.send(message);

                emailMst.setStatus(STATUS_SENT);
            } catch (Exception e) {
                emailMst.setRetryCount(emailMst.getRetryCount() + 1);
                emailMst.setStatus(maxRetryCount >= emailMst.getRetryCount() ? STATUS_FAILED : STATUS_UNSENT);
            }
        }

        commonEmailMstRepository.saveAll(unsentEmailsList);
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
