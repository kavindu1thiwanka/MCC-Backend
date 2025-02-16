package com.bms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "common_email_template")
public class CommonEmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "template_data", nullable = false, columnDefinition = "TEXT")
    private String templateData;

    @Column(name = "status", nullable = false)
    private Character status;
}
