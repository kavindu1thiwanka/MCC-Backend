package com.bms.entity;

import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Date;

import static com.bms.util.CommonConstants.STATUS_UNSENT;

@Entity
@Data
@NoArgsConstructor
@Table(name = "common_email_mst")
@EqualsAndHashCode(callSuper = true)
public class CommonEmailMst extends CommonBaseEntity implements Serializable {

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "send_to", nullable = false)
    private String sendTo;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "status", nullable = false)
    private Character status;

    public CommonEmailMst(String sendTo, String subject, String content) {
        this.sendTo = sendTo;
        this.subject = subject;
        this.content = content;
        this.status = STATUS_UNSENT;
        this.createdOn = new Date();
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.createdBy = user.getUsername();
    }
}
