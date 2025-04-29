package com.NikolaySHA.ExclusiveService.service;

import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.AddAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.EditAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    boolean create(AddAppointmentDTO data) throws MessagingException, GeneralSecurityException, IOException;
    
    List<Appointment> getAppointmentsByUserEmail(String email);
    
    Page<Appointment> getAllAppointmentsPaginated(Pageable pageable);
    
    Page<Appointment> searchAppointmentsPaginated(LocalDate date, String licensePlate, String make, String client, Status status, Pageable pageable);
    
    Optional<Appointment> findById(Long id);
    
    Optional<Appointment> findByIdInitializingUsersWithCars(Long id);
    
    void delete(Appointment appointment);
    
    boolean updateAppointment(Long id, EditAppointmentDTO appointment);
    
    List<Appointment> findByDate(LocalDate today);
    
    void saveAll(List<Appointment> appointments);
    
    void save(Appointment data);
    
    List<Appointment> findByLicensePlate(String licensePlate);
}
