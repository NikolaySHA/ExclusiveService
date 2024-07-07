package com.ExclusiveService.repo;

import com.ExclusiveService.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    
    List<Appointment> findByUser_Email(String email);
    List<Appointment> findByUser_EmailAndCar_LicensePlate(String email, String licensePlate);
}
