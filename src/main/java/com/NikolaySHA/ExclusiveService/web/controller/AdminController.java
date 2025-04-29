package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.AppointmentSearchDTO;
import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarSearchDTO;
import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserSearchDTO;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final CarService carService;
    
    public AdminController(UserService userService, AppointmentService appointmentService, CarService carService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.carService = carService;
    }
    
    @GetMapping({"/error/contact-admin"})
    public String errorContactAdmin() {
        return "error-contact-admin";
    }
    
    @GetMapping({"/garage/appointments"})
    @PreAuthorize("hasRole('ADMIN')")
    public String searchAppointments(Model model, @ModelAttribute("searchCriteria") AppointmentSearchDTO searchCriteria, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        model.addAttribute("statuses", Status.values());
        Pageable pageable = PageRequest.of(page, size, Sort.by(new String[]{"date"}));
        LocalDate date = null;
        if (searchCriteria.getDate() != null && !searchCriteria.getDate().isEmpty()) {
            try {
                date = LocalDate.parse(searchCriteria.getDate());
            } catch (Exception var9) {
                model.addAttribute("errorMessage", "Invalid date format");
                return "garage-appointments";
            }
        }
        
        Page appointmentPage;
        if (date == null && searchCriteria.getLicensePlate() == null && searchCriteria.getMake() == null && searchCriteria.getCustomer() == null && searchCriteria.getStatus() == null) {
            appointmentPage = this.appointmentService.getAllAppointmentsPaginated(pageable);
        } else {
            appointmentPage = this.appointmentService.searchAppointmentsPaginated(date, searchCriteria.getLicensePlate(), searchCriteria.getMake(), searchCriteria.getCustomer(), searchCriteria.getStatus(), pageable);
        }
        
        model.addAttribute("appointmentsData", appointmentPage);
        model.addAttribute("currentPage", appointmentPage.getNumber());
        model.addAttribute("totalPages", appointmentPage.getTotalPages());
        model.addAttribute("searchCriteria", searchCriteria);
        return "garage-appointments";
    }
    
    @GetMapping({"/garage/cars"})
    @PreAuthorize("hasRole('ADMIN')")
    public String searchCars(Model model, @ModelAttribute("searchCriteria") CarSearchDTO searchCriteria, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(new String[]{"licensePlate"}));
        Page carPage;
        if (searchCriteria.getVin() != null && !searchCriteria.getVin().isEmpty()) {
            carPage = this.carService.searchCars(searchCriteria.getLicensePlate(), searchCriteria.getMake(), searchCriteria.getVin(), searchCriteria.getCustomer(), pageable);
        } else {
            carPage = this.carService.searchCars(searchCriteria.getLicensePlate(), searchCriteria.getMake(), (String)null, searchCriteria.getCustomer(), pageable);
        }
        
        model.addAttribute("carsData", carPage.getContent());
        model.addAttribute("count", carPage.getTotalElements());
        model.addAttribute("currentPage", carPage.getNumber());
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("searchCriteria", searchCriteria);
        return "garage-cars";
    }
    
    @GetMapping({"/garage/users"})
    @PreAuthorize("hasRole('ADMIN')")
    public String searchCustomers(Model model, @ModelAttribute("searchCriteria") UserSearchDTO searchCriteria, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(new String[]{"name"}));
        model.addAttribute("userRoles", UserRolesEnum.values());
        Page userPage;
        if (searchCriteria.getName() == null && searchCriteria.getLicensePlate() == null && searchCriteria.getEmail() == null && searchCriteria.getRole() == null) {
            userPage = this.userService.findAllUsersWithRoles(pageable);
        } else {
            userPage = this.userService.searchUsers(searchCriteria.getName(), searchCriteria.getLicensePlate(), searchCriteria.getEmail(), searchCriteria.getRole(), pageable);
        }
        
        model.addAttribute("usersData", userPage);
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("count", userPage.getTotalElements());
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        return "garage-users";
    }
}
