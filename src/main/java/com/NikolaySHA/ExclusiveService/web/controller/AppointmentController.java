package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.AddAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.EditAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.dto.appointmentDTO.ShowAppointmentDTO;
import com.NikolaySHA.ExclusiveService.model.dto.expenseDTO.ExpenseInDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.model.entity.TransferProtocol;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.ExpenseService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping({"/appointments"})
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final CarService carService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ExpenseService expenseService;
    
    public AppointmentController(AppointmentService appointmentService, CarService carService, UserService userService, ModelMapper modelMapper, ExpenseService expenseService) {
        this.appointmentService = appointmentService;
        this.carService = carService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.expenseService = expenseService;
    }
    
    @ModelAttribute("appointmentData")
    public AddAppointmentDTO addAppointmentDTO() {
        return new AddAppointmentDTO();
    }
    
    @ModelAttribute("statuses")
    public List<Status> statuses() {
        return List.of(Status.values());
    }
    
    @ModelAttribute("carsData")
    public List<Car> cars() {
        return this.getCarList();
    }
    
    @GetMapping({"/add"})
    public String addAppointment(Model model, RedirectAttributes redirectAttributes) {
        List<Car> carsData = this.getCarList();
        if (carsData.isEmpty()) {
            redirectAttributes.addFlashAttribute("showErrorMessage", true);
            return "redirect:/cars/add";
        } else {
            model.addAttribute("isEdit", false);
            model.addAttribute("carsData", carsData);
            return "form-appointment";
        }
    }
    
    @PostMapping({"/add"})
    public String doAddAppointment(@Valid AddAppointmentDTO data, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws MessagingException, GeneralSecurityException, IOException {
        if (this.userService.findLoggedUser() == null) {
            return "redirect:/";
        } else if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("appointmentData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.appointmentData", bindingResult);
            return "redirect:/appointments/add";
        } else {
            boolean success = this.appointmentService.create(data);
            if (!success) {
                redirectAttributes.addFlashAttribute("appointmentData", data);
                return "redirect:/appointments/add";
            } else {
                return "redirect:/home";
            }
        }
    }
    
    @GetMapping({"/{id}"})
    public String viewAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        Appointment appointment = this.getValidatedAppointment(id, redirectAttributes);
        if (appointment == null) {
            return "redirect:/error/contact-admin";
        } else {
            List<ExpenseInDTO> expensesList = this.expenseService.GetExpensesForAppointment(id);
            List<TransferProtocol> transferProtocols = appointment.getProtocols();
            ShowAppointmentDTO data = (ShowAppointmentDTO)this.modelMapper.map(appointment, ShowAppointmentDTO.class);
            double totalExpenses = this.expenseService.getTotalExpensesForAppointment(id);
            model.addAttribute("showAppointmentData", data);
            model.addAttribute("expenses", expensesList);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("appointmentId", id);
            model.addAttribute("protocols", transferProtocols);
            return "view-appointment";
        }
    }
    
    @GetMapping({"/edit/{id}"})
    @Transactional
    public String editAppointmentForm(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("isEdit", true);
        Appointment appointment = this.getValidatedAppointment(id, redirectAttributes);
        if (appointment != null && !appointment.getStatus().equals(Status.COMPLETED) && !appointment.getStatus().equals(Status.IN_PROGRESS) && !appointment.getStatus().equals(Status.PENDING)) {
            EditAppointmentDTO appointmentDTO = (EditAppointmentDTO)this.modelMapper.map(appointment, EditAppointmentDTO.class);
            model.addAttribute("id", id);
            model.addAttribute("appointmentData", appointmentDTO);
            return "form-appointment";
        } else {
            redirectAttributes.addFlashAttribute("cantUpdateAppointment", true);
            return "redirect:/error/contact-admin";
        }
    }
    
    @PostMapping({"/edit/{id}"})
    @Transactional
    public String updateAppointment(@PathVariable("id") Long id, @Valid EditAppointmentDTO appointment, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (!this.userService.findLoggedUser().getId().equals(appointment.getUser().getId()) && !this.userService.loggedUserHasRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        } else if (bindingResult.hasErrors()) {
            model.addAttribute("appointmentData", appointment);
            model.addAttribute("org.springframework.validation.BindingResult.appointmentData", bindingResult);
            model.addAttribute("id", id);
            model.addAttribute("isEdit", true);
            return "form-appointment";
        } else {
            boolean success = this.appointmentService.updateAppointment(id, appointment);
            return !success ? "form-appointment" : "redirect:/appointments/" + id;
        }
    }
    
    @PostMapping({"/delete/{id}"})
    public String deleteAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Appointment appointment = this.getValidatedAppointment(id, redirectAttributes);
        if (appointment == null) {
            return "redirect:/error/contact-admin";
        } else {
            this.appointmentService.delete(appointment);
            return "redirect:/";
        }
    }
    
    private Appointment getValidatedAppointment(Long id, RedirectAttributes redirectAttributes) {
        Optional<Appointment> appointmentOptional = this.appointmentService.findById(id);
        if (appointmentOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return null;
        } else {
            Appointment appointment = (Appointment)appointmentOptional.get();
            if (!appointment.getUser().equals(this.userService.findLoggedUser()) && !this.userService.loggedUserHasRole("ADMIN")) {
                redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
                return null;
            } else {
                return appointment;
            }
        }
    }
    
    private List<Car> getCarList() {
        List carsData;
        if (!this.userService.loggedUserHasRole("ADMIN")) {
            carsData = this.carService.findCarsByUser(this.userService.findLoggedUser().getId());
        } else {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Page<Car> carPage = this.carService.findAllCars(pageable);
            carsData = carPage.getContent();
        }
        
        return carsData;
    }
}
