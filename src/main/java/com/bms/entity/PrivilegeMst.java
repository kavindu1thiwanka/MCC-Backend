package com.bms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Table(name = "privilege_mst")
public class PrivilegeMst implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "privilege_name")
    private String privilegeName;

    @Column(name = "privilege_code")
    private String privilegeCode;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "status")
    private Character status;
}
