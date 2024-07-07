package com.ExclusiveService.controller;

import com.ExclusiveService.model.dto.AddCarDataDTO;
import com.ExclusiveService.service.CarService;
import com.ExclusiveService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CarController {
    
    private final UserService userService;
    private final CarService carService;
    
    public CarController(UserService userService, CarService carService) {
        this.userService = userService;
        this.carService = carService;
    }
    
    @ModelAttribute("addCarData")
    public AddCarDataDTO addCarDataDTO(){
        return new AddCarDataDTO();
    }
    
    
    
    @GetMapping("/add-car")
    public String addCar(){
        return "car-add";
    }
    
    @PostMapping("/add-car")
    public String doAddCar(@Valid AddCarDataDTO data, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes){
        if (userService.findLoggedUser() == null) {
            return "redirect:/";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("addCarData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addCarData", bindingResult);
            
            return "redirect:/add-car";
        }
        boolean success = carService.addCar(data);
        if (!success) {
            redirectAttributes.addFlashAttribute("addCarData", data);
            return "redirect:/add-car";
        }
        return "redirect:/";
    }
  
}
