package com.NikolaySHA.ExclusiveService.web.aop;

import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.ProtocolService;
import com.NikolaySHA.ExclusiveService.service.impl.GmailSender;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AppointmentStatusAspect {
    private final ProtocolService protocolService;
    private final AppointmentService appointmentService;
    private final GmailSender emailSender;
    
    public AppointmentStatusAspect(ProtocolService protocolService, AppointmentService appointmentService, GmailSender emailSender) {
        this.protocolService = protocolService;
        this.appointmentService = appointmentService;
        this.emailSender = emailSender;
    }
    
    @Pointcut("execution(* com.NikolaySHA.ExclusiveService.service.impl.UpdateAppointmentStatusService.updateAppointmentStatus(..)) && args(appointment, newStatus)")
    public void updateAppointmentStatusPointcut(Appointment appointment, Status newStatus) {
    }
    
    @After("updateAppointmentStatusPointcut(appointment, newStatus)")
    @Transactional
    public void issueTransferProtocolAfterUpdateAppointmentStatus(Appointment appointment, Status newStatus) {
        if (newStatus.equals(Status.IN_PROGRESS) || newStatus.equals(Status.COMPLETED)) {
            Optional<Appointment> updatedAppointment = this.appointmentService.findById(appointment.getId());
            ProtocolService var10001 = this.protocolService;
            Objects.requireNonNull(var10001);
            updatedAppointment.ifPresent(var10001::createTransferProtocol);
        }
        
    }
    
    @After("updateAppointmentStatusPointcut(appointment, newStatus)")
    @Transactional
    public void sendEmailAfterUpdateAppointmentStatus(Appointment appointment, Status newStatus) throws MessagingException, GeneralSecurityException, IOException {
        User user = appointment.getCar().getOwner();
        switch (newStatus) {
            case PENDING:
                this.emailSender.sendMail("Записан час за сервиз", String.format("Вие имате записан час, днес - %s,\n за вашия автомобил: %s, Марка: %s, Модел: %s.\n Очакваме ви!", appointment.getDate(), appointment.getCar().getLicensePlate(), appointment.getCar().getMake(), appointment.getCar().getModel()), user.getEmail());
                this.emailSender.sendMail("Предстоящ ремонт", String.format("Очаквайте днес - %s,\n автомобил: %s, Марка: %s, Модел: %s.\n !", appointment.getDate(), appointment.getCar().getLicensePlate(), appointment.getCar().getMake(), appointment.getCar().getModel()), "exclautoservice@gmail.com");
                break;
            case IN_PROGRESS:
                this.emailSender.sendMail("Приет автомобил", String.format("Вашия автомобил: %s, Марка: %s, Модел: %s.\n е приет за ремонт. Ще ви информираме при промяна на статуса на ремонта.", appointment.getCar().getLicensePlate(), appointment.getCar().getMake(), appointment.getCar().getModel()), user.getEmail());
                break;
            case COMPLETED:
                this.emailSender.sendMail("Завършен ремонт", String.format("Вашия автомобил: %s, Марка: %s, Модел: %s.\n е готов. Заповядайте да си го получите.", appointment.getCar().getLicensePlate(), appointment.getCar().getMake(), appointment.getCar().getModel()), user.getEmail());
        }
        
    }
}
