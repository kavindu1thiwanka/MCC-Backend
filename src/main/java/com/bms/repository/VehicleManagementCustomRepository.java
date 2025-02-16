package com.bms.repository;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.VehicleMstDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleManagementCustomRepository {

    List<VehicleMstDto> getVehicleList(CommonFilterDto commonFilterDto);
}
