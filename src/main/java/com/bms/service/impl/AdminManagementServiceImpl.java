package com.bms.service.impl;

import com.bms.entity.ReservationMst;
import com.bms.entity.UserMst;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.AdminManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bms.util.CommonConstants.*;

@Service
public class AdminManagementServiceImpl implements AdminManagementService {

    private UserMstRepository userMstRepository;
    private ReservationMstRepository reservationMstRepository;

    /**
     * This method is used to get dashboard details for admin
     */
    @Override
    public ResponseEntity<Object> loadDashboardDetails() {

        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userMstRepository.getAllActiveUsersCount());

        List<UserMst> driverList = userMstRepository.getAllActiveDrivers();
        stats.put("totalDrivers", driverList.size());
        response.put("pieChartData", getDriverAvailabilityStats(driverList));

        setReservationStats(response, stats);

        response.put("stats", stats);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This helper method is used to set reservation stats
     * cancelled and completed reservation statistics of last month
     * and active reservation count
     */
    private void setReservationStats(HashMap<String, Object> response, HashMap<String, Object> stats) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        List<ReservationMst> reservationList = reservationMstRepository.getReservationDetailsAfterDate(calendar.getTime());

        List<String> lineChartLabels = new ArrayList<>();

        Integer[] completedReservationArray = new Integer[31];
        Integer[] cancelledReservationArray = new Integer[31];
        int activeReservationCount = 0;

        for (ReservationMst reservation : reservationList) {

            if (!lineChartLabels.contains(reservation.getPickUpDate().toString())
                    && (reservation.getStatus().equals(STATUS_COMPLETE) || reservation.getStatus().equals(STATUS_RESERVATION_CANCELLED))) {
               lineChartLabels.add(reservation.getPickUpDate().toString().split(" ")[0]);
            }

            int index = lineChartLabels.indexOf(reservation.getPickUpDate().toString().split(" ")[0]);

            if (reservation.getStatus().equals(STATUS_COMPLETE)) {
                completedReservationArray[index] = completedReservationArray[index] == null ? 1 : completedReservationArray[index] + 1;
            } else if (reservation.getStatus().equals(STATUS_RESERVATION_CANCELLED)) {
                cancelledReservationArray[index] = cancelledReservationArray[index] == null ? 1 : cancelledReservationArray[index] + 1;
            } else if (reservation.getStatus().equals(STATUS_ACTIVE)) { // || reservation.getOnTrip()
                activeReservationCount = activeReservationCount + 1;
            }
        }

        cancelledReservationArray = Arrays.stream(cancelledReservationArray).map(i -> i == null ? 0 : i).toArray(Integer[]::new);
        completedReservationArray = Arrays.stream(completedReservationArray).map(i -> i == null ? 0 : i).toArray(Integer[]::new);

        HashMap<String, Object> reservationStatsMap = new HashMap<>();
        reservationStatsMap.put("labels", lineChartLabels);
        reservationStatsMap.put("datasets", Arrays.asList(completedReservationArray, cancelledReservationArray));
        response.put("lineChartData", reservationStatsMap);
        stats.put("activeRides", activeReservationCount);
    }

    /**
     * This helper method is used to get driver availability stats
     * like online, offline and onTrip driver count
     */
    private Integer[] getDriverAvailabilityStats(List<UserMst> driverList) {

        Integer onlineDriverCount = 0;
        Integer offlineDriverCount = 0;
        Integer onTripDriverCount = 0;

        for (UserMst driver : driverList) {
            if (driver.getIsOnline().equals(STATUS_YES)) {
                onlineDriverCount = onlineDriverCount + 1;
            } else if (driver.getIsOnline().equals(STATUS_NO)) {
                offlineDriverCount = offlineDriverCount + 1;
            } else if (Boolean.TRUE.equals(driver.getOnTrip())) {
                onTripDriverCount = onTripDriverCount + 1;
            }
        }

        return new Integer[]{onlineDriverCount, offlineDriverCount, onTripDriverCount};
    }

    @Autowired
    public void setUserMstRepository(UserMstRepository userMstRepository) {
        this.userMstRepository = userMstRepository;
    }

    @Autowired
    public void setReservationMstRepository(ReservationMstRepository reservationMstRepository) {
        this.reservationMstRepository = reservationMstRepository;
    }
}
