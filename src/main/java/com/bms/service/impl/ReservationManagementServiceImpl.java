package com.bms.service.impl;

import com.bms.dto.ReservationDto;
import com.bms.entity.ReservationMst;
import com.bms.entity.TransactionMst;
import com.bms.entity.UserMst;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.TransactionMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.ReservationManagementService;
import com.bms.service.StripeService;
import com.bms.util.BMSCheckedException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;

@Service
public class ReservationManagementServiceImpl implements ReservationManagementService {

    private ReservationMstRepository reservationMstRepository;
    private UserMstRepository userMstRepository;
    private TransactionMstRepository transactionMstRepository;

    private StripeService stripeService;

    /**
     * This method is used to create new reservation
     *
     * @param reservationDto reservation details
     * @return 201 HttpStatus if successfully created
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Map<String, String>> createReservation(ReservationDto reservationDto) throws BMSCheckedException, StripeException {

        validateTransaction(reservationDto);

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ReservationMst reservationMst = new ReservationMst(reservationDto);
        reservationMst.setUserId(user.getId());
        reservationMst.setCreatedBy(user.getEmail());
        reservationMst.setCreatedOn(new Date());

        if (Boolean.TRUE.equals(reservationDto.getNeedDriver())) {
            setDriverId(reservationMst);
        }
        reservationMstRepository.save(reservationMst);

        TransactionMst transactionMst = new TransactionMst();
        transactionMst.setReservationId(reservationMst.getId());
        transactionMst.setAmount(reservationDto.getTotalCost());
        transactionMst.setPaymentType(PAYMENT_TYPE_CARD);
        transactionMst.setPaymentDate(new Date());
        transactionMst.setStatus(STATUS_TRANSACTION_PENDING);
        transactionMst.setCreatedBy(user.getEmail());
        transactionMst.setCreatedOn(new Date());
        transactionMstRepository.save(transactionMst);

        HashMap<String, Object> map = new HashMap<>();
        map.put(STRING_CURRENCY, "LKR");
        map.put(STRING_AMOUNT, transactionMst.getAmount());
        map.put(STRING_TRANSACTION_ID, transactionMst.getId());

        return stripeService.createCheckoutSession(map);
    }

    /**
     * This method is used to update reservation's status and transaction status
     *
     * @param trxId         transaction id
     * @param paymentStatus payment status
     * @return HttpStatus
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> updateReservationDetails(Integer trxId, Character paymentStatus) throws BMSCheckedException {
        Optional<TransactionMst> transactionOpt = transactionMstRepository.findById(trxId);

        if (transactionOpt.isEmpty()) {
            throw new BMSCheckedException(TRANSACTION_NOT_FOUND);
        }

        TransactionMst transactionMst = transactionOpt.get();
        transactionMst.setStatus(paymentStatus);
        transactionMst.setUpdateOn(new Date());
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        transactionMst.setUpdateBy(user.getUsername());
        transactionMstRepository.save(transactionMst);

        Optional<ReservationMst> reservationOpt = reservationMstRepository.findById(transactionMst.getReservationId());

        if (reservationOpt.isEmpty()) {
            throw new BMSCheckedException(RESERVATION_NOT_FOUND);
        }

        ReservationMst reservationMst = reservationOpt.get();
        reservationMst.setPaymentStatus(paymentStatus);
        reservationMst.setStatus(paymentStatus.equals(STATUS_COMPLETE) ? STATUS_ACTIVE : STATUS_FAILED);
        reservationMst.setUpdateOn(new Date());
        reservationMst.setUpdateBy(user.getUsername());
        reservationMstRepository.save(reservationMst);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to get logged-in user's reservation details
     */
    @Override
    public ResponseEntity<Object> getLoggedInUserReservationDetails() {
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(reservationMstRepository.getReservationDetailsByCreatedUser(user.getUsername()), HttpStatus.OK);
    }

    /**
     * This method is used to update reservation status
     */
    @Override
    public ResponseEntity<Object> updateReservationStatus(Integer reservationId, Character status) throws BMSCheckedException {
        Optional<ReservationMst> reservationMstOpt = reservationMstRepository.findById(reservationId);

        if (reservationMstOpt.isEmpty()) {
            throw new BMSCheckedException(RESERVATION_NOT_FOUND);
        }

        ReservationMst reservationMst = reservationMstOpt.get();
        reservationMst.setStatus(status);
        reservationMst.setUpdateOn(new Date());
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reservationMst.setUpdateBy(user.getUsername());
        reservationMstRepository.save(reservationMst);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to get active reservation details
     */
    @Override
    public ResponseEntity<Object> getActiveReservationDetails() throws BMSCheckedException {
        return new ResponseEntity<>(reservationMstRepository.getReservationDetailsByStatus(Arrays.asList(STATUS_ACTIVE)), HttpStatus.OK);
    }

    /**
     * This method is used to get reservation history details
     */
    @Override
    public ResponseEntity<Object> getReservationHistoryDetails() throws BMSCheckedException {
        return new ResponseEntity<>(reservationMstRepository.getReservationDetailsByStatus(Arrays.asList(STATUS_COMPLETE, STATUS_RESERVATION_CANCELLED)), HttpStatus.OK);
    }

    /**
     * This method is used to get reservation details of provided reservation id
     */
    @Override
    public ResponseEntity<Object> getReservationDetails(Integer reservationId) {
        ReservationDto reservationDto = reservationMstRepository.getReservationDetailsById(reservationId);

        if (reservationDto == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        if (reservationDto.getUserId() != null) {
            Optional<UserMst> customerOpt = userMstRepository.findById(reservationDto.getUserId());

            customerOpt.ifPresent(reservationDto::setCustomerDetails);
        }

        if (Boolean.TRUE.equals(reservationDto.getNeedDriver())) {
            Optional<UserMst> driverOpt = userMstRepository.findById(reservationDto.getDriverId());

            driverOpt.ifPresent(reservationDto::setDriverDetails);
        }

        return new ResponseEntity<>(reservationDto, HttpStatus.OK);
    }

    /**
     * This method is used to set driver id
     */
    private void setDriverId(ReservationMst reservationMst) throws BMSCheckedException {
        List<UserMst> allDrivers = userMstRepository.getAllAvailableDrivers();

        if (!allDrivers.isEmpty()) {
            reservationMst.setDriverId(allDrivers.getFirst().getId());
            return;
        }

        Integer driverId = reservationMstRepository
                .getReservationUnavailableDrivers(reservationMst.getPickUpDate(), reservationMst.getReturnDate())
                .getFirst();

        if (driverId == null) {
            throw new BMSCheckedException(DRIVERS_NOT_AVAILABLE);
        }

        reservationMst.setDriverId(driverId);
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

    @Autowired
    public void setTransactionMstRepository(TransactionMstRepository transactionMstRepository) {
        this.transactionMstRepository = transactionMstRepository;
    }

    @Autowired
    public void setStripeService(StripeService stripeService) {
        this.stripeService = stripeService;
    }
}
