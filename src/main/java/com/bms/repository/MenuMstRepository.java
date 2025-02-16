package com.bms.repository;

import com.bms.entity.MenuMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

import static com.bms.util.CommonConstants.STATUS_ACTIVE;

public interface MenuMstRepository extends JpaRepository<MenuMst, Integer> {

    @Query("SELECT menu FROM MenuMst menu INNER JOIN MenuPrivileges mp ON menu.id = mp.menuId " +
            "INNER JOIN RolePrivileges rp ON mp.privilegeId = rp.privilegeId " +
            "INNER JOIN RoleMst role ON rp.roleId = role.id " +
            "WHERE role.id = :roleId AND menu.status = '" + STATUS_ACTIVE + "' AND role.status = '" + STATUS_ACTIVE + "'" +
            "ORDER BY menu.displayOrder ASC")
    Set<MenuMst> getMenuListByRoleId(@Param("roleId") Integer roleId);
}
