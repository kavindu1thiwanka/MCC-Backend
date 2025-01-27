package com.bms.entity;

import com.bms.dto.RoleManagementDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.bms.util.CommonConstant.STATUS_ACTIVE;

@Entity
@Data
@NoArgsConstructor
@Table(name = "role_mst")
public class RoleMst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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
