package com.bms.repository;

import com.bms.entity.CommonEmailMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import static com.bms.util.CommonConstants.STATUS_SENT;
import static com.bms.util.CommonConstants.STATUS_UNSENT;

public interface CommonEmailMstRepository extends JpaRepository<CommonEmailMst, Integer> {

    @Query("SELECT mst FROM CommonEmailMst mst WHERE mst.status ='" + STATUS_UNSENT + "'")
    List<CommonEmailMst> getAllUnsentEmails();
}
