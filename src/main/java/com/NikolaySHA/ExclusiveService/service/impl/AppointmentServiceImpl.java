package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.AddAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.EditAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.repo.AppointmentRepository;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final GmailSender emailSender;
    private final UpdateAppointmentStatusService updateAppointmentStatusService;
    
    public boolean create(AddAppointmentDTO data) throws MessagingException, GeneralSecurityException, IOException {
        Appointment appointment = (Appointment)this.modelMapper.map(data, Appointment.class);
        appointment.setUser(data.getCar().getOwner());
        appointment.setStatus(Status.SCHEDULED);
        this.appointmentRepository.save(appointment);
        this.emailSender.sendMail("Записан нов час за сервиз", String.format("Вие записахте час за %s,\n за вашия автомобил: %s, марка: %s, модел: %s.\n Ще ви очакваме на посочената дата!", appointment.getDate(), appointment.getCar().getLicensePlate(), appointment.getCar().getMake(), appointment.getCar().getModel()), appointment.getUser().getEmail());
        this.emailSender.sendMail("Записан нов час", String.format("Очаквайте на %s,\n автомобил: %s, марка: %s, модел: %s!", appointment.getDate(), appointment.getCar().getLicensePlate(), appointment.getCar().getMake(), appointment.getCar().getModel()), "exclautoservice@gmail.com");
        return true;
    }
    
    public List<Appointment> getAppointmentsByUserEmail(String email) {
        List<Appointment> appointments = new ArrayList();
        List<Appointment> byCustomerEmail = this.appointmentRepository.findByUser_Email(email);
        if (!byCustomerEmail.isEmpty()) {
            appointments = byCustomerEmail;
        }
        
        return (List)appointments;
    }
    
    public Page<Appointment> getAllAppointmentsPaginated(Pageable pageable) {
        return this.appointmentRepository.findAll(pageable);
    }
    
    public Page<Appointment> searchAppointmentsPaginated(LocalDate date, String licensePlate, String make, String client, Status status, Pageable pageable) {
        return this.appointmentRepository.searchAppointmentsPaginated(date, licensePlate, make, client, status, pageable);
    }
    
    public List<Appointment> findByDate(LocalDate today) {
        return this.appointmentRepository.findByDate(today);
    }
    
    public Optional<Appointment> findById(Long id) {
        return this.appointmentRepository.findById(id);
    }
    
    public void delete(Appointment appointment) {
        this.appointmentRepository.delete(appointment);
    }
    
    public boolean updateAppointment(Long id, EditAppointmentDTO appointment) {
        Optional<Appointment> toEdit = this.appointmentRepository.findById(id);
        if (toEdit.isEmpty()) {
            return false;
        } else {
            Appointment editedAppointment = (Appointment)toEdit.get();
            if (appointment.getComment() != null && !appointment.getComment().equals(editedAppointment.getComment())) {
                editedAppointment.setComment(appointment.getComment());
            }
            
            if (appointment.getDate() != null && !appointment.getDate().equals(editedAppointment.getDate())) {
                editedAppointment.setDate(appointment.getDate());
            }
            
            if (appointment.getUser() != null && !appointment.getUser().equals(editedAppointment.getUser())) {
                editedAppointment.setUser(appointment.getUser());
            }
            
            if (appointment.getPaintDetails() != null && !appointment.getPaintDetails().equals(editedAppointment.getPaintDetails())) {
                editedAppointment.setPaintDetails(appointment.getPaintDetails());
            }
            
            if (appointment.getPaymentMethod() != null && !appointment.getPaymentMethod().equals(editedAppointment.getPaymentMethod())) {
                editedAppointment.setPaymentMethod(appointment.getPaymentMethod());
            }
            
            if (appointment.getCar() != null && !appointment.getCar().equals(editedAppointment.getCar())) {
                editedAppointment.setCar(appointment.getCar());
            }
            
            if (appointment.getStatus() != null && !appointment.getStatus().equals(editedAppointment.getStatus())) {
                this.updateAppointmentStatusService.updateAppointmentStatus(editedAppointment, appointment.getStatus());
            }
            
            this.appointmentRepository.save(editedAppointment);
            return true;
        }
    }
    
    public void save(Appointment data) {
        this.appointmentRepository.save(data);
    }
    
    public Optional<Appointment> findByIdInitializingUsersWithCars(Long id) {
        return this.appointmentRepository.findByIdWithUserAndCars(id);
    }
    
    public void saveAll(List<Appointment> appointments) {
        this.appointmentRepository.saveAll(appointments);
    }
    
    public List<Appointment> findByLicensePlate(String licensePlate) {
        return this.appointmentRepository.findByCar_LicensePlate(licensePlate);
    }
    
    public AppointmentServiceImpl(final AppointmentRepository appointmentRepository, final ModelMapper modelMapper, final GmailSender emailSender, final UpdateAppointmentStatusService updateAppointmentStatusService) {
        this.appointmentRepository = appointmentRepository;
        this.modelMapper = modelMapper;
        this.emailSender = emailSender;
        this.updateAppointmentStatusService = updateAppointmentStatusService;
    }
}
