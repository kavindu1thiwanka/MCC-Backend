package com.bms.service.impl;

import com.bms.dto.RoleManagementDto;
import com.bms.entity.RoleMst;
import com.bms.entity.RolePrivileges;
import com.bms.entity.UserMst;
import com.bms.repository.PrivilegeMstRepository;
import com.bms.repository.RoleMstRepository;
import com.bms.repository.RolePrivilegesRepository;
import com.bms.service.RoleManagementService;
import com.bms.util.BMSCheckedException;
import com.bms.util.ExceptionMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;

@Service
@Transactional
public class RoleManagementServiceImpl implements RoleManagementService {

    private RoleMstRepository roleMstRepository;
    private RolePrivilegesRepository rolePrivilegesRepository;
    private PrivilegeMstRepository privilegeMstRepository;

    /**
     * This method is used to create new role
     *
     * @param roleManagementDto new role's details
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> createRole(RoleManagementDto roleManagementDto) {

        // TODO need to validate before saving

        RoleMst roleMst = new RoleMst(roleManagementDto);
        roleMstRepository.save(roleMst);

        List<RolePrivileges> rolePrivileges = new ArrayList<>();
        for (Integer privilegeId : roleManagementDto.getPrivilegeIds()) {
            rolePrivileges.add(RolePrivileges.builder()
                    .roleId(roleMst.getId())
                    .privilegeId(privilegeId)
                    .build()
            );
        }

        rolePrivilegesRepository.saveAll(rolePrivileges);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * This method is used to get all roles in the system
     *
     * @return List of roles
     */
    @Override
    public ResponseEntity<Object> getAllRoles() {
        Set<RoleMst> roleList = roleMstRepository.getAllByMainRole();

        for (RoleMst mainRole : roleList) {
            mainRole.setSubRoleList(roleMstRepository.findByMainRole(mainRole.getId()));
        }

        return new ResponseEntity<>(roleList, HttpStatus.OK);
    }

    /**
     * This method is used to get role-wise privileges
     *
     * @param roleId role id
     * @return List of privileges related to the role
     */
    @Override
    public ResponseEntity<Object> getRoleWisePrivilegeList(Integer roleId) throws BMSCheckedException {

        if (roleId == null) {
            throw new BMSCheckedException(ROLE_ID_CANNOT_BE_EMPTY);
        }

        if (!roleMstRepository.existsById(roleId)) {
            throw new BMSCheckedException(ROLE_NOT_FOUND);
        }

        return new ResponseEntity<>(rolePrivilegesRepository.getAllPrivilegesByRoleId(roleId), HttpStatus.OK);
    }

    /**
     * This method is used to activate role
     *
     * @param roleId role id
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> activateRole(Integer roleId) throws BMSCheckedException {
        if (roleId == null) {
            throw new BMSCheckedException(ROLE_ID_CANNOT_BE_EMPTY);
        }

        roleMstRepository.save(getStatusUpdatedRole(roleId, STATUS_ACTIVE));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to activate provided list of roles
     *
     * @param roleIdList Role id list
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> activateRoleBulk(List<Integer> roleIdList) throws BMSCheckedException {

        if (roleIdList == null || roleIdList.isEmpty()) {
            throw new BMSCheckedException(ROLE_IDS_CANNOT_BE_EMPTY);
        }

        List<RoleMst> roleMstList = new ArrayList<>();
        for (Integer roleId : roleIdList) {
            roleMstList.add(getStatusUpdatedRole(roleId, STATUS_ACTIVE));
        }

        roleMstRepository.saveAll(roleMstList);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to inactivate role
     *
     * @param roleId role id
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> inactivateRole(Integer roleId) throws BMSCheckedException {
        if (roleId == null) {
            throw new BMSCheckedException(ROLE_ID_CANNOT_BE_EMPTY);
        }

        roleMstRepository.save(getStatusUpdatedRole(roleId, STATUS_INACTIVE));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to inactivate provided list of roles
     *
     * @param roleIdList Role id list
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> inactivateRoleBulk(List<Integer> roleIdList) throws BMSCheckedException {
        if (roleIdList == null || roleIdList.isEmpty()) {
            throw new BMSCheckedException(ROLE_IDS_CANNOT_BE_EMPTY);
        }

        List<RoleMst> roleMstList = new ArrayList<>();
        for (Integer roleId : roleIdList) {
            roleMstList.add(getStatusUpdatedRole(roleId, STATUS_INACTIVE));
        }

        roleMstRepository.saveAll(roleMstList);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to delete role
     *
     * @param roleId role id
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> deleteRole(Integer roleId) throws BMSCheckedException {
        if (roleId == null) {
            throw new BMSCheckedException(ROLE_ID_CANNOT_BE_EMPTY);
        }

        roleMstRepository.save(getStatusUpdatedRole(roleId, STATUS_DELETE));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to delete provided list of roles
     *
     * @param roleIdList Role id list
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> deleteRoleBulk(List<Integer> roleIdList) throws BMSCheckedException {
        if (roleIdList == null || roleIdList.isEmpty()) {
            throw new BMSCheckedException(ROLE_IDS_CANNOT_BE_EMPTY);
        }

        List<RoleMst> roleMstList = new ArrayList<>();
        for (Integer roleId : roleIdList) {
            roleMstList.add(getStatusUpdatedRole(roleId, STATUS_DELETE));
        }

        roleMstRepository.saveAll(roleMstList);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to get status updated role
     *
     * @param roleId role id
     * @param status status
     * @return Updated RoleMst
     */
    private RoleMst getStatusUpdatedRole(Integer roleId, Character status) throws BMSCheckedException {
        Optional<RoleMst> roleMstOpt = roleMstRepository.findById(roleId);

        if (roleMstOpt.isEmpty()) {
            throw new BMSCheckedException(ROLE_NOT_FOUND);
        }

        RoleMst roleMst = roleMstOpt.get();
        roleMst.setStatus(status);
        // TODO need to add updatedBy and updatedOn

        return roleMst;
    }

    /**
     * This method is used to get logged-in user's privileges
     *
     * @return List of privileges
     */
    @Override
    public ResponseEntity<Object> getLoggedInUserPrivilegeList() throws BMSCheckedException {

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getRoleId() == null) {
            throw new BMSCheckedException(ROLE_ID_CANNOT_BE_EMPTY);
        }

        return new ResponseEntity<>(privilegeMstRepository.findPrivilegeIdByRoleId(user.getRoleId()), HttpStatus.OK);
    }

    @Autowired
    public void setRoleMstRepository(RoleMstRepository roleMstRepository) {
        this.roleMstRepository = roleMstRepository;
    }

    @Autowired
    public void setRolePrivilegesRepository(RolePrivilegesRepository rolePrivilegesRepository) {
        this.rolePrivilegesRepository = rolePrivilegesRepository;
    }

    @Autowired
    public void setPrivilegeMstRepository(PrivilegeMstRepository privilegeMstRepository) {
        this.privilegeMstRepository = privilegeMstRepository;
    }
}
