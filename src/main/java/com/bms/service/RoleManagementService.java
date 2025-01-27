package com.bms.service;

import com.bms.dto.RoleManagementDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleManagementService {

    ResponseEntity<Object> createRole(RoleManagementDto roleManagementDto);

    ResponseEntity<Object> getAllRoles();

    ResponseEntity<Object> getRoleWisePrivilegeList(Integer roleId);

    ResponseEntity<Object> activateRole(Integer roleId);

    ResponseEntity<Object> activateRoleBulk(List<Integer> roleIdList);

    ResponseEntity<Object> inactivateRole(Integer roleId);

    ResponseEntity<Object> inactivateRoleBulk(List<Integer> roleIdList);

    ResponseEntity<Object> deleteRole(Integer roleId);

    ResponseEntity<Object> deleteRoleBulk(List<Integer> roleIdList);

    ResponseEntity<Object> getLoggedInUserPrivilegeList();

}
