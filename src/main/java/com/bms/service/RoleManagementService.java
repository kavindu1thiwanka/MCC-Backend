package com.bms.service;

import com.bms.dto.RoleManagementDto;
import com.bms.util.BMSCheckedException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleManagementService {

    ResponseEntity<Object> createRole(RoleManagementDto roleManagementDto);

    ResponseEntity<Object> getAllRoles();

    ResponseEntity<Object> getRoleWisePrivilegeList(Integer roleId) throws BMSCheckedException;

    ResponseEntity<Object> activateRole(Integer roleId) throws BMSCheckedException;

    ResponseEntity<Object> activateRoleBulk(List<Integer> roleIdList) throws BMSCheckedException;

    ResponseEntity<Object> inactivateRole(Integer roleId) throws BMSCheckedException;

    ResponseEntity<Object> inactivateRoleBulk(List<Integer> roleIdList) throws BMSCheckedException;

    ResponseEntity<Object> deleteRole(Integer roleId) throws BMSCheckedException;

    ResponseEntity<Object> deleteRoleBulk(List<Integer> roleIdList) throws BMSCheckedException;

    ResponseEntity<Object> getLoggedInUserPrivilegeList() throws BMSCheckedException;

}
