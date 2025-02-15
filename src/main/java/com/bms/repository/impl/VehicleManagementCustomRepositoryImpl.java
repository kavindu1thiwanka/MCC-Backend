package com.bms.repository.impl;

import com.bms.dto.CommonFilterDto;
import com.bms.dto.VehicleMstDto;
import com.bms.repository.VehicleManagementCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bms.util.CommonConstants.*;

@Service
public class VehicleManagementCustomRepositoryImpl implements VehicleManagementCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<VehicleMstDto> getVehicleList(CommonFilterDto commonFilterDto) {

        String sql = "SELECT new com.bms.dto.VehicleMstDto(car.vehicleNo, car.vehicleModel, car.vehicleType, car.seats, car.gearType, car.vehicleImage) " +
                "FROM VehicleMst car WHERE car.status='A' AND car.availability='Y' AND car.category='" +  commonFilterDto.getCategory() + "'";
        sql = setConditions(sql, commonFilterDto);
        sql = setOrderBy(sql, commonFilterDto);
        Query query = entityManager.createQuery(sql);
        return query.getResultList();
    }

    private String setConditions(String sql, CommonFilterDto commonFilterDto) {

        if (commonFilterDto.getFilters() == null || commonFilterDto.getFilters().isEmpty()) {
            return sql;
        }

        for (String filter : commonFilterDto.getFilters()) {
            sql = sql.concat(EMPTY_SPACE_STRING).concat(SQL_AND).concat(EMPTY_SPACE_STRING).concat(filter);
        }

        return sql;
    }

    private String setOrderBy(String sql, CommonFilterDto commonFilterDto) {
        if (commonFilterDto.getSortBy() == null || commonFilterDto.getSortBy().isEmpty()) {
            return sql;
        }

        return sql.concat(EMPTY_SPACE_STRING).concat(SQL_ORDER_BY).concat(EMPTY_SPACE_STRING).concat(commonFilterDto.getSortBy());
    }
}
