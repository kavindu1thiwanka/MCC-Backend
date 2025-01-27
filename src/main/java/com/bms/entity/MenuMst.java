package com.bms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
@Table(name = "menu_mst")
public class MenuMst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "route")
    private String route;

    @Column(name = "icon")
    private String icon;

    @Column(name = "type")
    private Character menuType;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "main_role")
    private Integer mainRole;

    @Column(name = "status")
    private Character status;

    @Transient
    private Set<MenuMst> subMenuList = new HashSet<>();

}
