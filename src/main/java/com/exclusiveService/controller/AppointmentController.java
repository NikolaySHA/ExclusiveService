package com.exclusiveService.controller;


import com.exclusiveService.model.dto.AddAppointmentDTO;
import com.exclusiveService.model.entity.Car;
import com.exclusiveService.service.AppointmentService;
import com.exclusiveService.service.CarService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    private final CarService carService;
    
    public AppointmentController(AppointmentService appointmentService, CarService carService) {
        this.appointmentService = appointmentService;
        this.carService = carService;
    }
    
    @ModelAttribute("appointmentData")
    public AddAppointmentDTO addAppointmentDTO(){
        return new AddAppointmentDTO();
    }
    
    @GetMapping("/add-appointment")
    public String addAppointment(Model model){
       
//        List<Car> myCarsData = carService.findCarsByUser(userSession.getEmail());
//        if (myCarsData.isEmpty()){
//            return "redirect:/add-car";
//        }
//        model.addAttribute("myCarsData", myCarsData);
        return "appointment-add";
    }
    @PostMapping("/add-appointment")
    public String doAddAppointment(
            @Valid AddAppointmentDTO data,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes){
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("appointmentData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.appointmentData", bindingResult);
            
            return "redirect:/add-appointment";
        }
        boolean success = appointmentService.create(data);
        
        if (!success) {
            redirectAttributes.addFlashAttribute("appointmentData", data);
            return "redirect:/add-appointment";
        }
        return "redirect:/home";
    }
}
