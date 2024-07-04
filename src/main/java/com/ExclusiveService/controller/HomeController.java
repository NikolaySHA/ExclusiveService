package com.ExclusiveService.controller;

import com.ExclusiveService.model.CustomerUserDetails;
import com.ExclusiveService.model.entity.Appointment;
import com.ExclusiveService.model.entity.Car;
import com.ExclusiveService.model.entity.User;
import com.ExclusiveService.service.AppointmentService;
import com.ExclusiveService.service.CarService;
import com.ExclusiveService.service.UserService;
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
