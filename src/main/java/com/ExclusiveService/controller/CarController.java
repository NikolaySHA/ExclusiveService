package com.ExclusiveService.controller;

import com.ExclusiveService.model.dto.AddCarDataDTO;
import com.ExclusiveService.service.CarService;
import com.ExclusiveService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String doAddCar(@Valid AddCarDataDTO data){
        
        boolean success = carService.addCar(data);
        if (!success) {
            return "redirect:/add-car";
        }
        return "redirect:/";
    }
  
}
