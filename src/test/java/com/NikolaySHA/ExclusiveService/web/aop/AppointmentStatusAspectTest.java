package com.NikolaySHA.ExclusiveService.web.aop;

import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.ProtocolService;
import com.NikolaySHA.ExclusiveService.service.impl.GmailSender;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentStatusAspectTest {
    
    @Mock
    private ProtocolService protocolService;
    
    @Mock
    private AppointmentService appointmentService;
    
    @Mock
    private GmailSender emailSender;
    
    @InjectMocks
    private AppointmentStatusAspect appointmentStatusAspect;
    
    private Appointment appointment;
    private User user;
    private Car car;
    
    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("testuser@example.com");
        
        car = new Car();
        car.setLicensePlate("123ABC");
        car.setMake("Toyota");
        car.setModel("Corolla");
        car.setOwner(user);  // Ensure the car has an owner
        
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Status.SCHEDULED);
        appointment.setUser(user);
        appointment.setCar(car);
        appointment.setDate(LocalDate.now());
    }
    
    @Test
    public void testAfterUpdateAppointmentStatus_withInProgress() throws MessagingException, GeneralSecurityException, IOException {
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        
        appointmentStatusAspect.issueTransferProtocolAfterUpdateAppointmentStatus(appointment, Status.IN_PROGRESS);
        
        verify(protocolService, times(1)).createTransferProtocol(appointment);
    }
    
    @Test
    public void testAfterUpdateAppointmentStatus_withCompleted() throws MessagingException, GeneralSecurityException, IOException {
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        
        appointmentStatusAspect.issueTransferProtocolAfterUpdateAppointmentStatus(appointment, Status.COMPLETED);
        
        verify(protocolService, times(1)).createTransferProtocol(appointment);
    }
    
    @Test
    public void testAfterUpdateAppointmentStatus_withOtherStatus() throws MessagingException, GeneralSecurityException, IOException {
        appointmentStatusAspect.issueTransferProtocolAfterUpdateAppointmentStatus(appointment, Status.SCHEDULED);
        
        verify(protocolService, times(0)).createTransferProtocol(any(Appointment.class));
    }
    
    @Test
    public void testAfterUpdateAppointmentStatus_withNonExistentAppointment() throws MessagingException, GeneralSecurityException, IOException {
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.empty());
        
        appointmentStatusAspect.issueTransferProtocolAfterUpdateAppointmentStatus(appointment, Status.IN_PROGRESS);
        
        verify(protocolService, times(0)).createTransferProtocol(any(Appointment.class));
    }
    
    @Test
    public void testSendEmailAfterUpdateAppointmentStatus_withInProgress() throws MessagingException, GeneralSecurityException, IOException {
        appointmentStatusAspect.sendEmailAfterUpdateAppointmentStatus(appointment, Status.IN_PROGRESS);
        
        verify(emailSender, times(1)).sendMail(
                eq("Приет автомобил"),
                contains("Вашия автомобил: 123ABC"),
                eq(user.getEmail())
                
        );
    }
    
    @Test
    public void testSendEmailAfterUpdateAppointmentStatus_withCompleted() throws MessagingException, GeneralSecurityException, IOException {
        appointmentStatusAspect.sendEmailAfterUpdateAppointmentStatus(appointment, Status.COMPLETED);
        
        verify(emailSender, times(1)).sendMail(
                eq("Завършен ремонт"),
                contains("Вашия автомобил: 123ABC"),
                eq(user.getEmail())
        );
    }
    
    @Test
    public void testSendEmailAfterUpdateAppointmentStatus_withPending() throws MessagingException, GeneralSecurityException, IOException {
        appointmentStatusAspect.sendEmailAfterUpdateAppointmentStatus(appointment, Status.PENDING);
        
        verify(emailSender, times(1)).sendMail(
                eq("Записан час за сервиз"),
                contains("Вие имате записан час, днес - " + appointment.getDate()),
                eq(user.getEmail())
        );
        verify(emailSender, times(1)).sendMail(
                eq("Предстоящ ремонт"),
                contains("Очаквайте днес - " + appointment.getDate()),
                eq("exclautoservice@gmail.com")
        );
    }
    
    @Test
    public void testSendEmailAfterUpdateAppointmentStatus_withScheduled() throws MessagingException, GeneralSecurityException, IOException {
        appointmentStatusAspect.sendEmailAfterUpdateAppointmentStatus(appointment, Status.SCHEDULED);
        
        verify(emailSender, times(0)).sendMail(anyString(), anyString(), anyString());
    }
}
