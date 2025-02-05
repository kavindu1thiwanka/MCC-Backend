package com.bms.repository;

import com.bms.entity.RoleMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

import static com.bms.util.CommonConstants.STATUS_DELETE;

public interface RoleMstRepository extends JpaRepository<RoleMst, Integer> {

    @Query("SELECT mst FROM RoleMst mst WHERE mst.mainRole IS NULL AND mst.status <> '" + STATUS_DELETE + "'")
    Set<RoleMst> getAllByMainRole();

    @Query("SELECT mst FROM RoleMst mst WHERE mst.mainRole = :mainRole AND mst.status <> '" + STATUS_DELETE + "'")
    Set<RoleMst> findByMainRole(Integer mainRole);

    @Query("SELECT mst.mainRole FROM RoleMst mst WHERE mst.id IN (:roleIds) AND mst.status <> '" + STATUS_DELETE + "'")
    Set<Integer> getMainRoleIdList(@Param("roleIds") Set<Integer> roleIds);
}
