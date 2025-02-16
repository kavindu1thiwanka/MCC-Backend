package com.bms.repository;

import com.bms.entity.UserMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.bms.util.CommonConstants.STATUS_ACTIVE;

public interface UserMstRepository extends JpaRepository<UserMst, Integer> {

    @Query("SELECT mst FROM UserMst mst WHERE mst.username = :username AND mst.status = '" + STATUS_ACTIVE + "'")
    Optional<UserMst> findByUsername(@Param("username") String username);

    @Query("SELECT mst FROM UserMst mst WHERE mst.email = :email AND mst.status <> :status")
    Optional<UserMst> findByEmailAndStatusNot(@Param("email") String email, @Param("status") Character status);

    @Query("SELECT mst FROM UserMst mst WHERE mst.uuid = :uuid")
    Optional<UserMst> findUserByUuid(@Param("uuid") String uuid);
}
