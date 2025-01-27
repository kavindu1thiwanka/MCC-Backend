package com.bms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "menu_privileges")
public class MenuPrivileges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "menu_id")
    private Integer menuId;

    @Column(name = "privilege_id")
    private Integer privilegeId;
}
