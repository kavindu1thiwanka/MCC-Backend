package com.bms.repository;

import com.bms.entity.PrivilegeMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

import static com.bms.util.CommonConstant.STATUS_ACTIVE;

public interface PrivilegeMstRepository extends JpaRepository<PrivilegeMst, Integer> {

    @Query("SELECT priv.privilegeCode FROM PrivilegeMst priv INNER JOIN RolePrivileges rp ON priv.id = rp.privilegeId " +
            "WHERE rp.roleId=:roleId AND priv.status='" + STATUS_ACTIVE + "'")
    Set<String> findPrivilegeIdByRoleId(@Param("roleId") Integer roleId);
}
