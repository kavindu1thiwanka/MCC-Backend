package com.bms.repository;

import com.bms.entity.UserMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static com.bms.util.CommonConstants.*;

public interface UserMstRepository extends JpaRepository<UserMst, Integer> {

    @Query("SELECT mst FROM UserMst mst WHERE mst.username = :username AND mst.status = '" + STATUS_ACTIVE + "'")
    Optional<UserMst> findByUsername(@Param("username") String username);

    @Query("SELECT mst FROM UserMst mst WHERE mst.email = :email AND mst.status <> :status")
    Optional<UserMst> findByEmailAndStatusNot(@Param("email") String email, @Param("status") Character status);

    @Query("SELECT mst FROM UserMst mst WHERE mst.email = :email AND mst.roleId = :roleId AND mst.status <> :status")
    Optional<UserMst> findByEmailAndRoleIdAndStatusNot(@Param("email") String email, @Param("roleId") Integer roleId, @Param("status") Character status);

    @Query("SELECT mst FROM UserMst mst WHERE mst.uuid = :uuid")
    Optional<UserMst> findUserByUuid(@Param("uuid") String uuid);

    @Query("SELECT mst FROM UserMst mst WHERE mst.roleId = " + ROLE_ID_DRIVER + " AND mst.status = '" + STATUS_ACTIVE + "' " +
            "AND mst.isOnline = '" + STATUS_YES + "' AND mst.id NOT IN (SELECT res.driverId FROM ReservationMst res WHERE res.status = 'A')")
    List<UserMst> getAllAvailableDrivers();

    @Query("SELECT mst.isOnline FROM UserMst mst WHERE mst.id = :driverId")
    Character getDriverOnlineStatus(@Param("driverId") Integer driverId);

    @Query("SELECT COUNT(mst) FROM UserMst mst WHERE mst.status = '" + STATUS_ACTIVE + "' AND mst.roleId = " + ROLE_ID_CUSTOMER + "")
    Integer getAllActiveUsersCount();

    @Query("SELECT mst FROM UserMst mst WHERE mst.status = '" + STATUS_ACTIVE + "' AND mst.roleId = " + ROLE_ID_DRIVER + "")
    List<UserMst> getAllActiveDrivers();

    @Query("SELECT mst FROM UserMst mst WHERE mst.status <> '" + STATUS_DELETE + "' AND mst.roleId = " + ROLE_ID_CUSTOMER + "")
    List<UserMst> getAllUsers();

    @Query("SELECT mst FROM UserMst mst WHERE mst.status <> '" + STATUS_DELETE + "' AND mst.roleId = " + ROLE_ID_DRIVER + "")
    List<UserMst> getAllDrivers();

    @Query("SELECT mst FROM UserMst mst WHERE mst.status <> '" + STATUS_DELETE + "' AND mst.roleId = " + ROLE_ID_ADMIN + "")
    List<UserMst> getAllAdmins();
}
