package com.bms.repository;

import com.bms.dto.ReservationDto;
import com.bms.entity.ReservationMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

import static com.bms.util.CommonConstants.*;

public interface ReservationMstRepository extends JpaRepository<ReservationMst, Integer> {

    @Query("SELECT res.vehicleNo,res FROM ReservationMst res INNER JOIN VehicleMst veh ON res.vehicleNo = veh.vehicleNo " +
            "WHERE res.status = '" + STATUS_ACTIVE + "' AND veh.category = :category AND veh.status='" + STATUS_ACTIVE + "'")
    List<Object[]> getAlreadyBookedVehicles(@Param("category") String category);

    @Query("SELECT res.driverId FROM ReservationMst res INNER JOIN UserMst usr ON res.driverId = usr.id " +
            "WHERE res.status = '" + STATUS_ACTIVE + "' AND usr.status = '" + STATUS_ACTIVE + "' AND usr.isOnline= '" + STATUS_YES + "'" +
            "AND (:pickUpDate NOT BETWEEN res.pickUpDate AND res.returnDate) AND (:returnDate NOT BETWEEN res.pickUpDate AND res.returnDate)")
    List<Integer> getReservationUnavailableDrivers(Date pickUpDate, Date returnDate);

    @Query("SELECT res FROM ReservationMst res WHERE res.createdBy = :username " +
            "AND res.status NOT IN ('" + STATUS_FAILED + "' , '" + STATUS_INACTIVE + "') ORDER BY res.id DESC")
    List<ReservationMst> getReservationDetailsByCreatedUser(@Param("username") String username);

    @Query("SELECT res FROM ReservationMst res WHERE res.driverId = :driverId AND res.status = '" + STATUS_ACTIVE + "' " +
            "AND res.pickUpDate >= :date ORDER BY res.pickUpDate ASC")
    List<ReservationMst> getUpcomingReservationsByDriverId(@Param("date") Date date, @Param("driverId") Integer driverId);

    @Query("SELECT res FROM ReservationMst res WHERE res.driverId = :driverId AND res.status IN ('" + STATUS_COMPLETE + "' , '" + STATUS_RESERVATION_CANCELLED + "') " +
            "AND res.returnDate < :date ORDER BY res.returnDate DESC")
    List<ReservationMst> getRideHistory(@Param("date") Date date, @Param("driverId") Integer driverId);

    @Query("SELECT res FROM ReservationMst res WHERE res.status NOT IN ('" + STATUS_FAILED + "' , '" + STATUS_INACTIVE + "') " +
            "AND res.pickUpDate >= :date ORDER BY res.pickUpDate ASC")
    List<ReservationMst> getReservationDetailsAfterDate(@Param("date") Date date);

    @Query("SELECT res FROM ReservationMst res WHERE res.status IN (:statusActive) ORDER BY res.createdOn DESC")
    List<ReservationMst> getReservationDetailsByStatus(List<Character> statusActive);

    @Query("SELECT new com.bms.dto.ReservationDto(res) FROM ReservationMst res WHERE res.id = :reservationId")
    ReservationDto getReservationDetailsById(@Param("reservationId") Integer reservationId);

    @Query("SELECT new com.bms.dto.ReservationDto(res, usr, veh, tran.amount) FROM ReservationMst res INNER JOIN UserMst usr ON res.userId = usr.id " +
            "INNER JOIN VehicleMst veh ON res.vehicleNo = veh.vehicleNo INNER JOIN TransactionMst tran ON res.id = tran.reservationId " +
            "WHERE res.pickUpDate BETWEEN :startDate AND :endDate AND res.status = '" + STATUS_COMPLETE + "'")
    List<ReservationDto> getReservationDetailsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT new com.bms.dto.ReservationDto(res, usr, veh, tran.amount) FROM ReservationMst res INNER JOIN UserMst usr ON res.userId = usr.id " +
            "INNER JOIN VehicleMst veh ON res.vehicleNo = veh.vehicleNo INNER JOIN TransactionMst tran ON res.id = tran.reservationId " +
            "WHERE res.status IN ('" + STATUS_COMPLETE + "' , '" + STATUS_RESERVATION_CANCELLED + "')")
    List<ReservationDto> getReservationDetails();

    @Query("SELECT new com.bms.dto.ReservationDto(res, tran.amount) FROM ReservationMst res INNER JOIN TransactionMst tran ON res.id = tran.reservationId " +
            "WHERE res.driverId = :driverId AND res.status = '" + STATUS_COMPLETE + "'")
    List<ReservationDto> findByDriverId(@Param("driverId") Integer driverId);

    @Query("SELECT new com.bms.dto.ReservationDto(res, tran.amount) FROM ReservationMst res INNER JOIN TransactionMst tran ON res.id = tran.reservationId " +
            "WHERE res.driverId = :driverId AND res.status = '" + STATUS_COMPLETE + "' AND res.pickUpDate BETWEEN :startDate AND :endDate")
    List<ReservationDto> findByDriverIdAndDateBetween(@Param("driverId") Integer driverId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
