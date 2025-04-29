package com.NikolaySHA.ExclusiveService.repo;

import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAll(Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE (:date IS NULL OR a.date >= :date) AND (:licensePlate IS NULL OR a.car.licensePlate LIKE %:licensePlate%) AND (:make IS NULL OR a.car.make LIKE %:make%) AND (:customer IS NULL OR a.user.name LIKE %:customer%) AND (:status IS NULL OR a.status = :status)")
    Page<Appointment> searchAppointmentsPaginated(@Param("date") LocalDate date, @Param("licensePlate") String licensePlate, @Param("make") String make, @Param("customer") String customer, @Param("status") Status status, Pageable pageable);
    
    List<Appointment> findByUser_Email(String email);
    
    List<Appointment> findByDate(LocalDate today);
    
    @Query("SELECT a FROM Appointment a JOIN FETCH a.user u LEFT JOIN FETCH u.cars WHERE a.id = :id")
    Optional<Appointment> findByIdWithUserAndCars(@Param("id") Long id);
    
    @Query("SELECT COALESCE(SUM(a.paintDetails), 0) FROM Appointment a WHERE a.date BETWEEN :startOfWeek AND :endOfWeek AND a.status != :completedStatus")
    int findTotalDetailsForWeek(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek, @Param("completedStatus") Status completedStatus);
    
    List<Appointment> findByCar_LicensePlate(String licensePlate);
}
