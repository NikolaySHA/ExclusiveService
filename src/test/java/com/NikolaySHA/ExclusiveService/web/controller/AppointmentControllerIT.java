package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.AddAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.ExpenseService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentControllerIT {
    
    @Mock
    private AppointmentService appointmentService;
    
    @Mock
    private CarService carService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private ExpenseService expenseService;
    
    @Mock
    private ModelMapper modelMapper;
    
    @InjectMocks
    private AppointmentController appointmentController;
    
    private User loggedInUser;
    private Appointment appointment;
    
    
    @BeforeEach
    void setUp() {
        loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setName("user");
        
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setUser(loggedInUser);
    }
    
    
    @Test
    void testDoAddAppointmentSuccess() throws MessagingException, GeneralSecurityException, IOException {
        AddAppointmentDTO dto = new AddAppointmentDTO();
        when(userService.findLoggedUser()).thenReturn(loggedInUser);
        when(appointmentService.create(any(AddAppointmentDTO.class))).thenReturn(true);
        
        String viewName = appointmentController.doAddAppointment(dto, mock(BindingResult.class), mock(RedirectAttributes.class));
        
        assertEquals("redirect:/home", viewName);
    }
    
    @Test
    void testDoAddAppointmentFailure() throws MessagingException, GeneralSecurityException, IOException {
        AddAppointmentDTO dto = new AddAppointmentDTO();
        when(userService.findLoggedUser()).thenReturn(loggedInUser);
        when(appointmentService.create(any(AddAppointmentDTO.class))).thenReturn(false);
        
        String viewName = appointmentController.doAddAppointment(dto, mock(BindingResult.class), mock(RedirectAttributes.class));
        
        assertEquals("redirect:/appointments/add", viewName);
    }
}
