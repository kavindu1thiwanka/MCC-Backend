package com.bms.service.impl;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.ReservationDto;
import com.bms.dto.VehicleMstDto;
import com.bms.entity.ReservationMst;
import com.bms.entity.UserMst;
import com.bms.entity.VehicleMst;
import com.bms.exception.BusinessException;
import com.bms.repository.ReservationMstRepository;
import com.bms.repository.VehicleManagementCustomRepository;
import com.bms.repository.VehicleMstRepository;
import com.bms.service.FileStorageService;
import com.bms.service.VehicleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.bms.util.CommonConstants.STATUS_ACTIVE;
import static com.bms.util.ExceptionMessages.*;

@Service
public class VehicleManagementServiceImpl implements VehicleManagementService {

    private VehicleManagementCustomRepository vehicleManagementCustomRepository;
    private ReservationMstRepository reservationMstRepository;
    private VehicleMstRepository vehicleMstRepository;
    private FileStorageService fileStorageService;

    /**
     * This method is used to get vehicle list based on filter
     *
     * @param commonFilterDto filter
     * @return List of vehicles
     */
    @Override
    public ResponseEntity<Object> getVehicleList(CommonFilterDto commonFilterDto) {

        throw new BusinessException("Exception Check");

//        List<VehicleMstDto> vehicleList = vehicleManagementCustomRepository.getVehicleList(commonFilterDto);
//        HashMap<String, List<ReservationMst>> reservationMap = getReservationMap(commonFilterDto.getCategory());
//
//        List<VehicleMstDto> availableVehicleList = new ArrayList<>();
//
//        for (VehicleMstDto vehicle : vehicleList) {
//            if (!reservationMap.containsKey(vehicle.getVehicleNo())) {
//                availableVehicleList.add(vehicle);
//                continue;
//            }
//
//            boolean unavailable = false;
//            for (ReservationMst reservation : reservationMap.get(vehicle.getVehicleNo())) {
//
//                unavailable = (commonFilterDto.getPickUpDate().after(reservation.getPickUpDate())
//                        && commonFilterDto.getPickUpDate().before(reservation.getReturnDate()))
//                        || (commonFilterDto.getReturnDate().after(reservation.getPickUpDate())
//                        && commonFilterDto.getReturnDate().before(reservation.getReturnDate()));
//
//                if (unavailable) {
//                    break;
//                }
//            }
//            if (!unavailable) {
//                availableVehicleList.add(vehicle);
//            }
//        }
//        return new ResponseEntity<>(availableVehicleList, HttpStatus.OK);
    }

    private HashMap<String, List<ReservationMst>> getReservationMap(String category) {
        List<Object[]> alreadyBookedVehicleList = reservationMstRepository.getAlreadyBookedVehicles(category);

        HashMap<String, List<ReservationMst>> reservationMap = new HashMap<>();

        for (Object[] reservation : alreadyBookedVehicleList) {
            reservationMap.computeIfPresent((String) reservation[0], (k, v) -> {
                v.add((ReservationMst) reservation[1]);
                return v;
            });
            reservationMap.computeIfAbsent((String) reservation[0], k -> {
                List<ReservationMst> reservationMstList = new ArrayList<>();
                reservationMstList.add((ReservationMst) reservation[1]);
                return reservationMstList;
            });
        }
        return reservationMap;
    }

    /**
     * This method is used to add vehicle
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> addVehicle(VehicleMstDto vehicleMstDto, MultipartFile vehicleImage) throws BusinessException, IOException {

        validateVehicleDetails(vehicleMstDto);

        VehicleMst vehicleMst = new VehicleMst();
        vehicleMst.setVehicleNo(vehicleMstDto.getVehicleNo());
        vehicleMst.setVehicleModel(vehicleMstDto.getName());
        vehicleMst.setVehicleType(vehicleMstDto.getVehicleType());
        vehicleMst.setCategory(vehicleMstDto.getCategory());
        vehicleMst.setSeats(vehicleMstDto.getSeats());
        vehicleMst.setGearType(vehicleMstDto.getGearType());
        vehicleMst.setPricePerDay(vehicleMstDto.getPricePerDay());
        vehicleMst.setStatus(STATUS_ACTIVE);

        if (vehicleImage != null) {
            vehicleMst.setVehicleImage(fileStorageService.uploadVehicleImage(vehicleImage, vehicleMstDto.getVehicleNo(), null));
        }

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        vehicleMst.setCreatedBy(user.getUsername());
        vehicleMst.setCreatedOn(new Date());
        vehicleMstRepository.save(vehicleMst);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private void validateVehicleDetails(VehicleMstDto vehicleMstDto) throws BusinessException {

        if (vehicleMstDto == null) {
            throw new BusinessException(VEHICLE_DETAILS_CANNOT_BE_NULL);
        }

        if (vehicleMstDto.getVehicleNo() == null || vehicleMstDto.getVehicleNo().isEmpty()) {
            throw new BusinessException(VEHICLE_NO_CANNOT_BE_EMPTY);
        } else if (vehicleMstRepository.existsByVehicleNo(vehicleMstDto.getVehicleNo())) {
            throw new BusinessException(VEHICLE_ALREADY_EXISTS);
        }

        if (vehicleMstDto.getName() == null || vehicleMstDto.getName().isEmpty()) {
            throw new BusinessException(VEHICLE_NAME_CANNOT_BE_EMPTY);
        }

        if (vehicleMstDto.getVehicleType() == null || vehicleMstDto.getVehicleType().isEmpty()) {
            throw new BusinessException(VEHICLE_TYPE_CANNOT_BE_EMPTY);
        }

        if (vehicleMstDto.getSeats() == null) {
            throw new BusinessException(SEATS_AMOUNT_CANNOT_BE_NULL);
        }

        if (vehicleMstDto.getGearType() == null) {
            throw new BusinessException(GEAR_TYPE_CANNOT_BE_EMPTY);
        }

        if (vehicleMstDto.getPricePerDay() == null) {
            throw new BusinessException(PRICE_PER_DAY_CANNOT_BE_NULL);
        }
    }

    /**
     * This method is used to calculate vehicle total cost
     *
     * @return total cost
     */
    @Override
    public ResponseEntity<Object> getVehicleTotalCost(ReservationDto reservationDto) {

        BigDecimal totalCost = BigDecimal.ZERO;

        long reservedDays = (reservationDto.getReturnDate().getTime() - reservationDto.getPickUpDate().getTime())
                / (1000 * 60 * 60 * 24);

        reservedDays = reservedDays == 0 ? 1 : reservedDays;

        totalCost = totalCost.add(BigDecimal.valueOf(reservedDays).multiply(reservationDto.getPricePerDay()));

        if (Boolean.TRUE.equals(reservationDto.getNeedDriver())) {
            totalCost = totalCost.add(BigDecimal.valueOf(reservedDays).multiply(BigDecimal.valueOf(1200)));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalCost", totalCost);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method is used to update vehicle
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> updateVehicle(VehicleMstDto vehicleMstDto, MultipartFile vehicleImage) throws BusinessException, IOException {

        VehicleMst existingVehicle = getExistingVehicle(vehicleMstDto.getVehicleNo());

        existingVehicle.setVehicleNo(vehicleMstDto.getVehicleNo());
        existingVehicle.setVehicleModel(vehicleMstDto.getName());
        existingVehicle.setVehicleType(vehicleMstDto.getVehicleType());
        existingVehicle.setCategory(vehicleMstDto.getCategory());
        existingVehicle.setSeats(vehicleMstDto.getSeats());
        existingVehicle.setGearType(vehicleMstDto.getGearType());
        existingVehicle.setPricePerDay(vehicleMstDto.getPricePerDay());

        if (vehicleImage != null) {
            existingVehicle.setVehicleImage(fileStorageService.uploadVehicleImage(vehicleImage, vehicleMstDto.getVehicleNo(),
                    existingVehicle.getVehicleImage()));
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to update vehicle status
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> updateVehicleStatus(String vehicleNumber, Character status) throws BusinessException {

        VehicleMst existingVehicle = getExistingVehicle(vehicleNumber);
        existingVehicle.setStatus(status);
        vehicleMstRepository.save(existingVehicle);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to get vehicle list
     */
    @Override
    public ResponseEntity<Object> getAllVehicleList() {
        return new ResponseEntity<>(vehicleMstRepository.getVehicleListGroupByCategory(), HttpStatus.OK);
    }

    /**
     * This method is used to get vehicle details
     */
    @Override
    public ResponseEntity<Object> getVehicleDetails(String vehicleNumber) throws BusinessException {
        Optional<VehicleMst> vehicleOpt = vehicleMstRepository.findByVehicleNo(vehicleNumber);

        if (vehicleOpt.isEmpty()) {
            throw new BusinessException(VEHICLE_NOT_FOUND);
        }

        return new ResponseEntity<>(vehicleOpt.get(), HttpStatus.OK);
    }

    /**
     * This method is used to get existing vehicle details
     */
    private VehicleMst getExistingVehicle(String vehicleNumber) throws BusinessException {
        Optional<VehicleMst> vehicleOpt = vehicleMstRepository.findByVehicleNo(vehicleNumber);

        if (vehicleOpt.isEmpty()) {
            throw new BusinessException(VEHICLE_NOT_FOUND);
        }

        return vehicleOpt.get();
    }

    @Autowired
    public void setVehicleManagementCustomRepository(VehicleManagementCustomRepository vehicleManagementCustomRepository) {
        this.vehicleManagementCustomRepository = vehicleManagementCustomRepository;
    }

    @Autowired
    public void setReservationMstRepository(ReservationMstRepository reservationMstRepository) {
        this.reservationMstRepository = reservationMstRepository;
    }

    @Autowired
    public void setVehicleMstRepository(VehicleMstRepository vehicleMstRepository) {
        this.vehicleMstRepository = vehicleMstRepository;
    }

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
}
