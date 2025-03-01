package com.bms.service.impl;

import com.bms.entity.ReservationMst;
import com.bms.entity.TransactionMst;
import com.bms.entity.UserMst;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.TransactionMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.DriverManagementService;
import com.bms.util.BMSCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.bms.util.CommonConstants.STATUS_NO;
import static com.bms.util.CommonConstants.STATUS_YES;
import static com.bms.util.ExceptionMessages.USER_NOT_FOUND;

@Service
public class DriverManagementServiceImpl implements DriverManagementService {

    private UserMstRepository userMstRepository;
    private TransactionMstRepository transactionMstRepository;
    private ReservationMstRepository reservationMstRepository;

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
    public ResponseEntity<Object> getDriverDashboardDetails() {

        UserMst driver = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HashMap<String, Object> responseObj = new HashMap<>();

        responseObj.put("isOnline", userMstRepository.getDriverOnlineStatus(driver.getId()).equals(STATUS_YES));

        List<TransactionMst> transactionList = transactionMstRepository.getDriverEarningsByDriverId(driver.getId());

        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal dailyEarnings = BigDecimal.ZERO;
        BigDecimal weeklyEarnings = BigDecimal.ZERO;
        BigDecimal monthlyEarnings = BigDecimal.ZERO;

        for (TransactionMst transaction : transactionList) {
            totalEarnings = totalEarnings.add(transaction.getAmount());

            Calendar transactionCalendar = Calendar.getInstance();
            transactionCalendar.setTime(transaction.getCreatedOn());

            Calendar currentCalendar = Calendar.getInstance();

            if (isSameDay(transactionCalendar, currentCalendar)) {
                dailyEarnings = dailyEarnings.add(transaction.getAmount());
            }

            if (isSameMonthYear(transactionCalendar, currentCalendar)) {
                monthlyEarnings = monthlyEarnings.add(transaction.getAmount());
            }

            if (isSameWeek(transactionCalendar, currentCalendar)) {
                weeklyEarnings = weeklyEarnings.add(transaction.getAmount());
            }
        }

        HashMap<String, Object> earningsObj = new HashMap<>();
        earningsObj.put("total", totalEarnings);
        earningsObj.put("daily", dailyEarnings);
        earningsObj.put("weekly", weeklyEarnings);
        earningsObj.put("monthly", monthlyEarnings);
        responseObj.put("earnings", earningsObj);

        List<ReservationMst> upcomingReservations = reservationMstRepository.getUpcomingReservationsByDriverId(new Date(), driver.getId());
        responseObj.put("upcomingRides", upcomingReservations);

        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getDriverRideHistory() {
        UserMst driver = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(reservationMstRepository.getRideHistory(new Date(), driver.getId()), HttpStatus.OK);
    }

    /**
     * Helper method to check if two dates are in the same day
     */
    private boolean isSameDay(Calendar transactionCalendar, Calendar currentCalendar) {
        return transactionCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && transactionCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
                && transactionCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Helper method to check if two dates are in the same month and year
     */
    private boolean isSameMonthYear(Calendar transactionCalendar, Calendar currentCalendar) {
        return transactionCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && transactionCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH);
    }

    /**
     * Helper method to check if two dates are in the same week and year
     */
    private boolean isSameWeek(Calendar transactionCalendar, Calendar currentCalendar) {
        return transactionCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && transactionCalendar.get(Calendar.WEEK_OF_YEAR) == currentCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    @Autowired
    public void setUserMstRepository(UserMstRepository userMstRepository) {
        this.userMstRepository = userMstRepository;
    }

    @Autowired
    public void setTransactionMstRepository(TransactionMstRepository transactionMstRepository) {
        this.transactionMstRepository = transactionMstRepository;
    }

    @Autowired
    public void setReservationMstRepository(ReservationMstRepository reservationMstRepository) {
        this.reservationMstRepository = reservationMstRepository;
    }
}
