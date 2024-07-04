package com.exclusiveService.controller;

import com.exclusiveService.model.CustomerUserDetails;
import com.exclusiveService.model.entity.Appointment;
import com.exclusiveService.model.entity.Car;
import com.exclusiveService.model.entity.User;
import com.exclusiveService.service.AppointmentService;
import com.exclusiveService.service.CarService;
import com.exclusiveService.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final CarService carService;
    
    public HomeController(AppointmentService appointmentService, UserService userService, CarService carService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.carService = carService;
    }
    
    
    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomerUserDetails userDetails, Model model) {
        
        if (userDetails instanceof CustomerUserDetails) {
            model.addAttribute("welcomeMessage", userDetails.getName());
            List<Appointment> appointmentsForCustomer = appointmentService.getAppointments(userDetails.getUsername());
            model.addAttribute("appointmentsData", appointmentsForCustomer);
            User loggedIn = userService.findLoggedUser();
            List<Car> myCars = carService.findCarsByUser(loggedIn.getEmail());
            model.addAttribute("myCarsData", myCars);
            return "redirect:/home";
        } else {
           // model.addAttribute("welcomeMessage", "Anonymous ");
            return "index";
        }
    }
    
    @GetMapping("/gallery")
    public String gallery(){
        return "gallery";
    }
}
