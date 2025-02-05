package com.bms.entity.abst;

import com.bms.util.CommonConstants;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
@EqualsAndHashCode(of = {"id"})
public class CommonBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Integer id;

    @Column(name = "created_by")
    protected String createdBy;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = CommonConstants.US_DATE_FORMATS_STRING)
    protected Date createdOn;

    @Column(name = "update_by")
    protected String updateBy;

    @Column(name = "update_on")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = CommonConstants.US_DATE_FORMATS_STRING)
    protected Date updateOn;

    @Column(name = "delete_by")
    protected String deleteBy;

    @Column(name = "delete_on")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = CommonConstants.US_DATE_FORMATS_STRING)
    protected Date deleteOn;

}
