package com.bms.repository;

import com.bms.entity.RolePrivileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface RolePrivilegesRepository extends JpaRepository<RolePrivileges, Integer> {

    @Query("SELECT rp.privilegeId FROM RolePrivileges rp WHERE rp.roleId = :roleId")
    Set<Integer> getAllPrivilegesByRoleId(Integer roleId);
}
