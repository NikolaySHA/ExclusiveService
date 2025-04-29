package com.NikolaySHA.ExclusiveService.util;

import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.impl.UpdateAppointmentStatusService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledOperations {
    private final AppointmentService appointmentService;
    private final UpdateAppointmentStatusService updateStatusService;
    
    public ScheduledOperations(AppointmentService appointmentService, UpdateAppointmentStatusService updateStatusService) {
        this.appointmentService = appointmentService;
        this.updateStatusService = updateStatusService;
    }
    
    @Scheduled(
            cron = "0 0 0 * * 1-5"
    )
    @Transactional
    public void updateAppointmentStatusAtMidnight() {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = this.appointmentService.findByDate(today);
        Iterator var3 = appointments.iterator();
        
        while(var3.hasNext()) {
            Appointment appointment = (Appointment)var3.next();
            if (appointment.getStatus().equals(Status.SCHEDULED)) {
                this.updateStatusService.updateAppointmentStatus(appointment, Status.PENDING);
            }
        }
        
        this.appointmentService.saveAll(appointments);
    }
}