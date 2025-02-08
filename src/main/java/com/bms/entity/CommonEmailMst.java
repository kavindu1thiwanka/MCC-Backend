package com.bms.entity;

import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "status", nullable = false)
    private Character status;
}
