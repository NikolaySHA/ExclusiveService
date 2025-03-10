package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarDataDTO;
import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarViewDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cars")
public class CarController {
    
    private final UserService userService;
    private final CarService carService;
    private final ModelMapper modelMapper;
    
    public CarController(UserService userService, CarService carService, ModelMapper modelMapper) {
        this.userService = userService;
        this.carService = carService;
        this.modelMapper = modelMapper;
    }
    
    @ModelAttribute("carData")
    public CarDataDTO carDataDTO() {
        return new CarDataDTO();
    }
    @GetMapping("/{id}")
    public String viewCar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        
        Optional<Car> car = carService.findById(id);
        if (!car.get().getOwner().equals(userService.findLoggedUser()) && !userService.loggedUserHasRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("noPrivilegeMessage", true);
            return "redirect:/error/contact-admin";
        }
        CarViewDTO data = modelMapper.map(car, CarViewDTO.class);
        model.addAttribute("carViewData", data);
        return "view-car";
    }
    @GetMapping("/add")
    public String addCar(Model model) {
        model.addAttribute("isEdit", false);
        return "form-car";
    }
    
    @PostMapping("/add")
    public String doAddCar(@Valid CarDataDTO data, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (userService.findLoggedUser() == null) {
            return "redirect:/";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("carData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.carData", bindingResult);
            return "redirect:/cars/add";
        }
        if (carService.findByLicensePlate(data.getLicensePlate()) || carService.existByVin(data.getVin())){
            redirectAttributes.addFlashAttribute("carData", data);
            redirectAttributes.addFlashAttribute("showErrorMessageExistingCar", true);
            return "redirect:/cars/add";
        }
        carService.addCar(data);
        return "redirect:/";
    }
    
    @GetMapping("/edit/{id}")
    public String editCarForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        
        Optional<Car> carOptional = carService.findById(id);
        if (carOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        Car car = carOptional.get();
        User loggedUser = userService.findLoggedUser();
        if (!car.getOwner().equals(loggedUser) && !userService.loggedUserHasRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        CarDataDTO carDataDTO = modelMapper.map(car, CarDataDTO.class);
        model.addAttribute("carData", carDataDTO);
        model.addAttribute("id", id);
        model.addAttribute("isEdit", true);
        return "form-car";
    }
    
    @PostMapping("/edit/{id}")
    @Transactional
    public String updateCar(@PathVariable("id") Long id, @Valid @ModelAttribute("carData") CarDataDTO car,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        Car existingCar = carService.findById(id).orElse(null);
        User loggedUser = userService.findLoggedUser();
        
        if (existingCar == null) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        
        Long ownerId = existingCar.getOwner().getId();
        if (!loggedUser.getId().equals(ownerId) && !userService.loggedUserHasRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        
        String vin = car.getVin();
        if (vin != null && vin.trim().isEmpty()) {
            vin = null;
        }
        
        if (vin != null) {
            Optional<Car> carWithSameVin = carService.findByVin(vin);
            if (carWithSameVin.isPresent() && !carWithSameVin.get().getId().equals(id)) {
                redirectAttributes.addFlashAttribute("vinExist", true);
                return "redirect:/error/contact-admin";
            }
        }
        
        car.setVin(vin);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("carData", car);
            model.addAttribute("org.springframework.validation.BindingResult.carData", bindingResult);
            model.addAttribute("id", id);
            model.addAttribute("isEdit", true);
            return "form-car";
        }
        
        carService.updateCar(id, car);
        return "redirect:/cars/" + id;
    }
    
    
    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Car> carOptional = carService.findById(id);
        if (carOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("noSuchCarErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        Car car = carOptional.get();
        List<Appointment> appointments = car.getAppointments();
        if (!appointments.isEmpty()) {
            redirectAttributes.addFlashAttribute("deleteCarErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        carService.delete(car);
        return "redirect:/";
    }
}

