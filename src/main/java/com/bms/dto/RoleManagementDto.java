package com.bms.dto;

import com.bms.entity.RoleMst;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoleManagementDto {

    private String roleName;
    private Integer mainRole;
    private List<Integer> privilegeIds;
    private List<RoleMst> roleMstList;
}
