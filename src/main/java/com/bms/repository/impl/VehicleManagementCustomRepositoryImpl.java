package com.bms.repository.impl;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.VehicleMstDto;
import com.bms.repository.VehicleManagementCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleManagementCustomRepositoryImpl implements VehicleManagementCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<VehicleMstDto> getVehicleList(CommonFilterDto commonFilterDto) {

        String sql = "SELECT new com.bms.dto.VehicleMstDto(car.vehicleNo, car.name, car.vehicleType, car.seats, car.gearType, car.vehicleImage) " +
                "FROM VehicleMst car WHERE car.status='A' AND car.availability='Y' ";
        setConditions(sql, commonFilterDto);
//        sql = setOrderBy(sql, commonFilterDto);
        Query query = entityManager.createQuery(sql);
//        query = setParameters(query, commonFilterDto);
        return query.getResultList();
    }

    private void setConditions(String sql, CommonFilterDto commonFilterDto) {

        commonFilterDto.getFilters().forEach((key, value) -> {
        });
    }
}
