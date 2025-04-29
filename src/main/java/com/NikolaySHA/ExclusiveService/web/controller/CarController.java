package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarDataDTO;
import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarViewDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/cars"})
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
    
    @GetMapping({"/{id}"})
    public String viewCar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        Optional<Car> car = this.carService.findById(id);
        if (!((Car)car.get()).getOwner().equals(this.userService.findLoggedUser()) && !this.userService.loggedUserHasRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("noPrivilegeMessage", true);
            return "redirect:/error/contact-admin";
        } else {
            CarViewDTO data = (CarViewDTO)this.modelMapper.map(car, CarViewDTO.class);
            model.addAttribute("carViewData", data);
            return "view-car";
        }
    }
    
    @GetMapping({"/add"})
    public String addCar(Model model) {
        model.addAttribute("isEdit", false);
        return "form-car";
    }
    
    @PostMapping({"/add"})
    public String doAddCar(@Valid CarDataDTO data, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (this.userService.findLoggedUser() == null) {
            return "redirect:/";
        } else if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("carData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.carData", bindingResult);
            return "redirect:/cars/add";
        } else if (!this.carService.findByLicensePlate(data.getLicensePlate()) && !this.carService.existByVin(data.getVin())) {
            this.carService.addCar(data);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("carData", data);
            redirectAttributes.addFlashAttribute("showErrorMessageExistingCar", true);
            return "redirect:/cars/add";
        }
    }
    
    @GetMapping({"/edit/{id}"})
    public String editCarForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Car> carOptional = this.carService.findById(id);
        if (carOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        } else {
            Car car = (Car)carOptional.get();
            User loggedUser = this.userService.findLoggedUser();
            if (!car.getOwner().equals(loggedUser) && !this.userService.loggedUserHasRole("ADMIN")) {
                redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
                return "redirect:/error/contact-admin";
            } else {
                CarDataDTO carDataDTO = (CarDataDTO)this.modelMapper.map(car, CarDataDTO.class);
                model.addAttribute("carData", carDataDTO);
                model.addAttribute("id", id);
                model.addAttribute("isEdit", true);
                return "form-car";
            }
        }
    }
    
    @PostMapping({"/edit/{id}"})
    @Transactional
    public String updateCar(@PathVariable("id") Long id, @ModelAttribute("carData") @Valid CarDataDTO car, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        Car existingCar = this.carService.findById(id).orElse(null);
        User loggedUser = this.userService.findLoggedUser();
        if (existingCar == null) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        } else {
            Long ownerId = existingCar.getOwner().getId();
            if (!loggedUser.getId().equals(ownerId) && !this.userService.loggedUserHasRole("ADMIN")) {
                redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
                return "redirect:/error/contact-admin";
            } else {
                String vin = car.getVin();
                if (vin != null && vin.trim().isEmpty()) {
                    vin = null;
                }
                
                if (vin != null) {
                    Optional<Car> carWithSameVin = this.carService.findByVin(vin);
                    if (carWithSameVin.isPresent() && !((Car)carWithSameVin.get()).getId().equals(id)) {
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
                } else {
                    this.carService.updateCar(id, car);
                    return "redirect:/cars/" + id;
                }
            }
        }
    }
    
    @PostMapping({"/delete/{id}"})
    public String deleteCar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Car> carOptional = this.carService.findById(id);
        if (carOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("noSuchCarErrorMessage", true);
            return "redirect:/error/contact-admin";
        } else {
            Car car = (Car)carOptional.get();
            List<Appointment> appointments = car.getAppointments();
            if (!appointments.isEmpty()) {
                redirectAttributes.addFlashAttribute("deleteCarErrorMessage", true);
                return "redirect:/error/contact-admin";
            } else {
                this.carService.delete(car);
                return "redirect:/";
            }
        }
    }
}
