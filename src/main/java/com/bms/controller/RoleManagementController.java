package com.bms.controller;

import com.bms.dto.RoleManagementDto;
import com.bms.service.RoleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestScope
@RequestMapping(ROLE)
public class RoleManagementController {

    private RoleManagementService roleManagementService;

    @PostMapping(CREATE_ROLE_V1)
    public ResponseEntity<Object> createRole(@RequestBody RoleManagementDto roleManagementDto) {
        return roleManagementService.createRole(roleManagementDto);
    }

    @GetMapping(GET_ALL_ROLES_V1)
    public ResponseEntity<Object> getAllRoles() {
        return roleManagementService.getAllRoles();
    }

    @GetMapping(GET_ROLE_PRIVILEGES_V1)
    public ResponseEntity<Object> getRoleWisePrivilegeList(@RequestParam Integer roleId) {
        return roleManagementService.getRoleWisePrivilegeList(roleId);
    }

    @PutMapping(ACTIVATE_ROLE_V1)
    public ResponseEntity<Object> activateRole(@RequestParam Integer roleId) {
        return roleManagementService.activateRole(roleId);
    }

    @PutMapping(ACTIVATE_ROLE_BULK_V1)
    public ResponseEntity<Object> activateRoleBulk(@RequestBody List<Integer> roleIdList) {
        return roleManagementService.activateRoleBulk(roleIdList);
    }

    @PutMapping(INACTIVATE_ROLE_V1)
    public ResponseEntity<Object> inactivateRole(@RequestParam Integer roleId) {
        return roleManagementService.inactivateRole(roleId);
    }

    @PutMapping(INACTIVATE_ROLE_BULK_V1)
    public ResponseEntity<Object> inactivateRoleBulk(@RequestBody List<Integer> roleIdList) {
        return roleManagementService.inactivateRoleBulk(roleIdList);
    }

    @PutMapping(DELETE_ROLE_V1)
    public ResponseEntity<Object> deleteRole(@RequestParam Integer roleId) {
        return roleManagementService.deleteRole(roleId);
    }

    @PutMapping(DELETE_ROLE_BULK_V1)
    public ResponseEntity<Object> deleteRoleBulk(@RequestBody List<Integer> roleIdList) {
        return roleManagementService.deleteRoleBulk(roleIdList);
    }

    @GetMapping(GET_USER_PRIVILEGES_V1)
    public ResponseEntity<Object> getLoggedInUserPrivilegeList() {
        return roleManagementService.getLoggedInUserPrivilegeList();
    }

    @Autowired
    public void setRoleManagementService(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }
}
