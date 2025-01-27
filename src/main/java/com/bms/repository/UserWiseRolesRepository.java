package com.bms.repository;

import com.bms.entity.UserWiseRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserWiseRolesRepository extends JpaRepository<UserWiseRoles, Integer> {

    @Query("SELECT ur.roleId FROM UserWiseRoles ur WHERE ur.userId = :userId")
    Set<Integer> getRoleIdListByUserId(@Param("userId") Integer userId);
}
