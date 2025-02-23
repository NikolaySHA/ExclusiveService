package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.AppointmentSearchDTO;
import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarSearchDTO;
import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserSearchDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


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
    @GetMapping("/error/contact-admin")
    public String errorContactAdmin(){
        return "error-contact-admin";
    }
    
@GetMapping("/garage/appointments")
@PreAuthorize("hasRole('ADMIN')")
public String searchAppointments(Model model,
                                 @ModelAttribute("searchCriteria") AppointmentSearchDTO searchCriteria,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size) {
    model.addAttribute("statuses", Status.values());
    
    Page<Appointment> appointmentPage;
    Pageable pageable = PageRequest.of(page, size, Sort.by("date"));
    
    LocalDate date = null;
    if (searchCriteria.getDate() != null && !searchCriteria.getDate().isEmpty()) {
        try {
            date = LocalDate.parse(searchCriteria.getDate());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid date format");
            return "garage-appointments";
        }
    }
    
    if (date != null || searchCriteria.getLicensePlate() != null ||
            searchCriteria.getMake() != null || searchCriteria.getCustomer() != null ||
            searchCriteria.getStatus() != null) {
        
        appointmentPage = appointmentService.searchAppointmentsPaginated(
                date, searchCriteria.getLicensePlate(),
                searchCriteria.getMake(), searchCriteria.getCustomer(),
                searchCriteria.getStatus(), pageable);
    } else {
        appointmentPage = appointmentService.getAllAppointmentsPaginated(pageable);
    }
    
    model.addAttribute("appointmentsData", appointmentPage);
    model.addAttribute("currentPage", appointmentPage.getNumber());
    model.addAttribute("totalPages", appointmentPage.getTotalPages());
    model.addAttribute("searchCriteria", searchCriteria);
    
    return "garage-appointments";
}
    @GetMapping("/garage/cars")
    @PreAuthorize("hasRole('ADMIN')")
    public String searchCars(Model model,
                             @ModelAttribute("searchCriteria") CarSearchDTO searchCriteria,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("licensePlate"));
        Page<Car> carPage;
        if (searchCriteria.getVin() == null || searchCriteria.getVin().isEmpty()) {
            carPage = carService.searchCars(
                    searchCriteria.getLicensePlate(),
                    searchCriteria.getMake(),
                    null,
                    searchCriteria.getCustomer(),
                    pageable
            );
        } else {
            carPage = carService.searchCars(
                    searchCriteria.getLicensePlate(),
                    searchCriteria.getMake(),
                    searchCriteria.getVin(),
                    searchCriteria.getCustomer(),
                    pageable
            );
        }
        model.addAttribute("carsData", carPage.getContent());
        model.addAttribute("count", carPage.getTotalElements());
        model.addAttribute("currentPage", carPage.getNumber());
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("searchCriteria", searchCriteria);
        
        return "garage-cars";
    }
    
    
    
    @GetMapping("/garage/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String searchCustomers(Model model,
                                     @ModelAttribute("searchCriteria") UserSearchDTO searchCriteria,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<User> userPage;
        model.addAttribute("userRoles", UserRolesEnum.values());
        if (searchCriteria.getName() != null || searchCriteria.getLicensePlate() != null || searchCriteria.getEmail() != null || searchCriteria.getRole() != null) {
            userPage = userService.searchUsers(searchCriteria.getName(), searchCriteria.getLicensePlate(), searchCriteria.getEmail(), searchCriteria.getRole(), pageable);
        } else {
            userPage = userService.findAllUsersWithRoles(pageable);
        }
        
        model.addAttribute("usersData", userPage);
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("count", userPage.getTotalElements());
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        
        return "garage-users";
    }
    
    
}
