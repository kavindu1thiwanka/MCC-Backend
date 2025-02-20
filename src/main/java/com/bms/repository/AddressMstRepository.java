package com.bms.repository;

import com.bms.entity.AddressMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressMstRepository extends JpaRepository<AddressMst, Integer> {

    @Query("SELECT add FROM AddressMst add INNER JOIN UserMst usr ON add.userId = usr.id " +
            "WHERE usr.username = :userName")
    AddressMst getAddressMstByUserName(@Param("userName") String userName);
}
