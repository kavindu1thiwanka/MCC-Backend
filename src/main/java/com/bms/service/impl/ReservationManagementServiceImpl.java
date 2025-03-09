package com.bms.service.impl;

import com.bms.dto.ReportDto;
import com.bms.dto.ReservationDto;
import com.bms.entity.*;
import com.bms.repository.*;
import com.bms.service.EmailService;
import com.bms.service.InvoiceService;
import com.bms.service.ReservationManagementService;
import com.bms.service.StripeService;
import com.bms.util.BMSCheckedException;
import com.stripe.exception.StripeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;

@Service
public class ReservationManagementServiceImpl implements ReservationManagementService {

    private ReservationMstRepository reservationMstRepository;
    private UserMstRepository userMstRepository;
    private TransactionMstRepository transactionMstRepository;
    private EmailService emailService;
    private StripeService stripeService;
    private CommonEmailTemplateRepository commonEmailTemplateRepository;
    private VehicleMstRepository vehicleMstRepository;
    private CommonEmailMstRepository commonEmailMstRepository;
    private InvoiceService invoiceService;

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

        if (reservationMst.getStatus().equals(STATUS_ACTIVE)) {
            sendReservationSuccessEmail(reservationMst, transactionMst.getAmount());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to send reservation success email
     */
    @Async
    public void sendReservationSuccessEmail(ReservationMst reservationMst, BigDecimal amount) throws BMSCheckedException {

        ReservationDto reservationDto = new ReservationDto(reservationMst);
        reservationDto.setTotalCost(amount);
        reservationDto.setCustomerDetails(userMstRepository.findById(reservationMst.getUserId()).get());
        if (reservationMst.getDriverId() != null) {
            reservationDto.setDriverDetails(userMstRepository.findById(reservationMst.getDriverId()).get());
        }
        reservationDto.setVehicleModel(vehicleMstRepository.getVehicleModelByVehicleNumber(reservationMst.getVehicleNo()));

        Optional<CommonEmailTemplate> templateOpt = commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_RESERVATION_SUCCESSFUL);

        if (templateOpt.isEmpty()) {
            throw new BMSCheckedException(EMAIL_TEMPLATE_NOT_FOUND);
        }

        CommonEmailTemplate emailTemplate = templateOpt.get();

        Document html = Jsoup.parse(emailTemplate.getTemplateData(), CHARACTER_TYPE);

        Element emailSendToElement = html.body().getElementById(PARAM_EMAIL_SEND_TO);
        emailSendToElement.html(reservationDto.getCustomerDetails().getFirstName().concat(EMPTY_SPACE_STRING).concat(reservationDto.getCustomerDetails().getLastName() == null ? EMPTY_STRING : reservationDto.getCustomerDetails().getLastName()));

        Element reservationIdElement = html.body().getElementById(PARAM_RESERVATION_ID);
        reservationIdElement.html("#" + reservationMst.getId());

        Element vehicleModelElement = html.body().getElementById(PARAM_VEHICLE_MODEL);
        vehicleModelElement.html(reservationDto.getVehicleModel());

        Element pickUpLocationElement = html.body().getElementById(PARAM_PICKUP_LOCATION);
        pickUpLocationElement.html(reservationDto.getPickUpLocation());

        Element dropOffLocationElement = html.body().getElementById(PARAM_DROPOFF_LOCATION);
        dropOffLocationElement.html(reservationDto.getReturnLocation());

        Element pickupDateElement = html.body().getElementById(PARAM_PICKUP_DATE);
        pickupDateElement.html(String.valueOf(reservationDto.getPickUpDate()));

        Element dropOffDateElement = html.body().getElementById(PARAM_DROPOFF_DATE);
        dropOffDateElement.html(String.valueOf(reservationDto.getReturnDate()));

        Element totalPriceElement = html.body().getElementById(PARAM_TOTAL_PRICE);
        totalPriceElement.html(amount.toString());

        CommonEmailMst email = new CommonEmailMst();
        email.setSendTo(reservationDto.getCustomerDetails().getEmail());
        email.setSubject(emailTemplate.getSubject());
        email.setContent(html.html());
        email.setStatus(STATUS_INACTIVE);
        commonEmailMstRepository.save(email);

        byte[] invoiceBytes = invoiceService.generateInvoice(reservationDto);

        emailService.sendEmailWithAttachment(email.getId(), invoiceBytes);

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
        if (status.equals(STATUS_COMPLETE)) {
            reservationMst.setOnTrip(Boolean.FALSE);
        }
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
     * This method is used to update onTrip status
     */
    @Override
    public ResponseEntity<Object> changeOnTripStatus(Integer reservationId) throws BMSCheckedException {

        Optional<ReservationMst> reservationMstOpt = reservationMstRepository.findById(reservationId);

        if (reservationMstOpt.isEmpty()) {
            throw new BMSCheckedException(RESERVATION_NOT_FOUND);
        }

        ReservationMst reservationMst = reservationMstOpt.get();
        reservationMst.setOnTrip(Boolean.TRUE);
        reservationMst.setUpdateOn(new Date());
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reservationMst.setUpdateBy(user.getUsername());
        reservationMstRepository.save(reservationMst);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public List<ReservationDto> getReservationDetailsList(ReportDto reportData) {

        if (reportData.getStartDate() == null || reportData.getEndDate() == null) {
            return reservationMstRepository.getReservationDetails();
        }

        return reservationMstRepository.getReservationDetailsByDate(reportData.getStartDate(), reportData.getEndDate());
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

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setCommonEmailTemplateRepository(CommonEmailTemplateRepository commonEmailTemplateRepository) {
        this.commonEmailTemplateRepository = commonEmailTemplateRepository;
    }

    @Autowired
    public void setVehicleMstRepository(VehicleMstRepository vehicleMstRepository) {
        this.vehicleMstRepository = vehicleMstRepository;
    }

    @Autowired
    public void setCommonEmailMstRepository(CommonEmailMstRepository commonEmailMstRepository) {
        this.commonEmailMstRepository = commonEmailMstRepository;
    }

    @Autowired
    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
}
