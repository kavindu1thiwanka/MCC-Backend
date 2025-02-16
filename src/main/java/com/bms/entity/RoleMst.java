package com.bms.entity;

import com.bms.dto.RoleManagementDto;
import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.bms.util.CommonConstants.STATUS_ACTIVE;

@Entity
@Data
@NoArgsConstructor
@Table(name = "role_mst")
@EqualsAndHashCode(callSuper = true)
public class RoleMst extends CommonBaseEntity implements Serializable {

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "main_role")
    private Integer mainRole;

    @Column(name = "status")
    private Character status;

    @Transient
    private Set<RoleMst> subRoleList = new HashSet<>();

    public RoleMst(RoleManagementDto roleManagementDto) {
        this.roleName = roleManagementDto.getRoleName();
        this.mainRole = roleManagementDto.getMainRole();
        this.status = STATUS_ACTIVE;
    }
}
