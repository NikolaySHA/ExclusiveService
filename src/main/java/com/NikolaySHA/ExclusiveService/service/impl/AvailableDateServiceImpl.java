package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.repo.AppointmentRepository;
import com.NikolaySHA.ExclusiveService.service.AvailableDateService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Service;

@Service
public class AvailableDateServiceImpl implements AvailableDateService {
    private final AppointmentRepository appointmentRepository;
    
    public LocalDate calculateNextAvailableDate(int detailsCount) {
        LocalDate tomorowDate = LocalDate.now().plusDays(1L);
        int weeklyLimit = 30;
        
        while(true) {
            while(tomorowDate.getDayOfWeek() == DayOfWeek.SATURDAY || tomorowDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                tomorowDate = tomorowDate.plusDays(1L);
            }
            
            int currentWeekLoad = this.getCurrentWeekLoad(tomorowDate);
            if (currentWeekLoad + detailsCount <= weeklyLimit) {
                return tomorowDate;
            }
            
            tomorowDate = tomorowDate.plusDays(1L);
        }
    }
    
    public int getCurrentWeekLoad(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        return this.appointmentRepository.findTotalDetailsForWeek(startOfWeek, endOfWeek, Status.COMPLETED);
    }
    
    public AvailableDateServiceImpl(final AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
}
