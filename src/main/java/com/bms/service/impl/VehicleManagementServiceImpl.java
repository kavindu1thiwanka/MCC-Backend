package com.bms.service.impl;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.VehicleMstDto;
import com.bms.repository.VehicleManagementCustomRepository;
import com.bms.service.VehicleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehicleManagementServiceImpl implements VehicleManagementService {

    private VehicleManagementCustomRepository vehicleManagementCustomRepository;

    /**
     * This method is used to get vehicle list based on filter
     *
     * @param commonFilterDto filter
     * @return List of vehicles
     */
    @Override
    public ResponseEntity<Object> getVehicleList(CommonFilterDto commonFilterDto) {
        return new ResponseEntity<>(vehicleManagementCustomRepository.getVehicleList(commonFilterDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> addVehicle(VehicleMstDto vehicleMstDto) {
        return null;
    }

    @Autowired
    public void setVehicleManagementCustomRepository(VehicleManagementCustomRepository vehicleManagementCustomRepository) {
        this.vehicleManagementCustomRepository = vehicleManagementCustomRepository;
    }
}
