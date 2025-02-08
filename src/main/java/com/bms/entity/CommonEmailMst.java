package com.bms.entity;

import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "common_email_mst")
@EqualsAndHashCode(callSuper = true)
public class CommonEmailMst extends CommonBaseEntity {

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "to", nullable = false)
    private String to;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "attachment")
    private String attachment;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "status", nullable = false)
    private Character status;
}
