package com.bms.entity;

import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
@Table(name = "menu_mst")
@EqualsAndHashCode(callSuper = true)
public class MenuMst extends CommonBaseEntity implements Serializable {

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
