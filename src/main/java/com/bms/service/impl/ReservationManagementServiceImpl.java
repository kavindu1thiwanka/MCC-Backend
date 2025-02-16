package com.bms.service.impl;

import com.bms.dto.ReservationDto;
import com.bms.entity.ReservationMst;
import com.bms.entity.UserMst;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.ReservationManagementService;
import com.bms.util.BMSCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.bms.util.ExceptionMessages.*;

@Service
@Transactional
public class ReservationManagementServiceImpl implements ReservationManagementService {

    private ReservationMstRepository reservationMstRepository;
    private UserMstRepository userMstRepository;

    /**
     * This method is used to create new reservation
     *
     * @param reservationDto reservation details
     * @return 201 HttpStatus if successfully created
     */
    @Override
    public ResponseEntity<Object> createReservation(ReservationDto reservationDto) throws BMSCheckedException {

        validateTransaction(reservationDto);

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ReservationMst reservationMst = new ReservationMst(reservationDto);
        reservationMst.setUserId(user.getId());
        reservationMst.setCreatedBy(user.getEmail());
        reservationMst.setCreatedOn(new Date());

        if (Boolean.TRUE.equals(reservationDto.getNeedDriver())) {
            List<UserMst> allDrivers = userMstRepository.getAllAvailableDrivers();

            if (!allDrivers.isEmpty()) {
                reservationMst.setDriverId(allDrivers.getFirst().getId());
            }

            reservationMst.setDriverId(reservationMstRepository
                    .getReservationUnavailableDrivers(reservationMst.getPickUpDate(), reservationMst.getReturnDate())
                    .getFirst());
        }
        reservationMstRepository.save(reservationMst);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * This method is used to validate reservation details
     *
     * @param reservationDto reservation details
     */
    private void validateTransaction(ReservationDto reservationDto) throws BMSCheckedException {
        if (reservationDto.getVehicleNo() == null || reservationDto.getVehicleNo().isEmpty()) {
            throw new BMSCheckedException(VEHICLE_NO_CANNOT_BE_EMPTY);
        }

        if (reservationDto.getPickUpDate() == null) {
            throw new BMSCheckedException(PICK_UP_DATE_CANNOT_BE_EMPTY);
        }

        if (reservationDto.getReturnDate() == null) {
            throw new BMSCheckedException(RETURN_DATE_CANNOT_BE_EMPTY);
        }

        if (reservationDto.getPickUpLocation() == null || reservationDto.getPickUpLocation().isEmpty()) {
            throw new BMSCheckedException(PICK_UP_LOCATION_CANNOT_BE_EMPTY);
        }
    }

    @Autowired
    public void setReservationMstRepository(ReservationMstRepository reservationMstRepository) {
        this.reservationMstRepository = reservationMstRepository;
    }

    @Autowired
    public void setUserMstRepository(UserMstRepository userMstRepository) {
        this.userMstRepository = userMstRepository;
    }
}
