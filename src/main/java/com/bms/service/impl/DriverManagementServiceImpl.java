package com.bms.service.impl;

import com.bms.entity.UserMst;
import com.bms.repository.UserMstRepository;
import com.bms.service.DriverManagementService;
import com.bms.util.BMSCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

import static com.bms.util.CommonConstants.STATUS_NO;
import static com.bms.util.CommonConstants.STATUS_YES;
import static com.bms.util.ExceptionMessages.USER_NOT_FOUND;

@Service
public class DriverManagementServiceImpl implements DriverManagementService {

    private UserMstRepository userMstRepository;

    /**
     * This method is used to update driver online status
     */
    @Override
    public ResponseEntity<Object> updateOnlineStatus(Boolean isOnline) throws BMSCheckedException {
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<UserMst> userOpt = userMstRepository.findById(user.getId());

        if (userOpt.isEmpty()) {
            throw new BMSCheckedException(USER_NOT_FOUND);
        }

        UserMst userMst = userOpt.get();
        userMst.setIsOnline(Boolean.TRUE.equals(isOnline) ? STATUS_YES : STATUS_NO);
        userMstRepository.save(userMst);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getOnlineStatus() {
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        HashMap<String, Object> responseObj = new HashMap<>();
        responseObj.put("isOnline", user.getIsOnline());

        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }

    @Autowired
    public void setUserMstRepository(UserMstRepository userMstRepository) {
        this.userMstRepository = userMstRepository;
    }
}
