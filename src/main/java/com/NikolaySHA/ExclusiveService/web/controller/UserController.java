package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserEditDTO;
import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserLoginDTO;
import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserRegisterDTO;
import com.NikolaySHA.ExclusiveService.model.dto.userDTO.UserViewDTO;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    private final ModelMapper modelMapper;
    
    @ModelAttribute("userData")
    public UserRegisterDTO userDTO() {
        return new UserRegisterDTO();
    }
    @ModelAttribute("loginData")
    public UserLoginDTO loginDTO(){
        return new UserLoginDTO();
    }
    
    @GetMapping("/login")
    public String viewLogin() {
        if (userService.findLoggedUser() != null) {
            return "redirect:/home";
        }
        return "login";
    }
    @GetMapping("/login-error")
    public String viewLoginError(Model model) {
        model.addAttribute("showErrorMessage", true);
        return "login";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("isEdit", false);
        return "form-user";
    }
    @PostMapping("/register")
    public String doRegisterUser(@Valid @ModelAttribute("userData") UserRegisterDTO data, BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        
        if (!data.getPassword().equals(data.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("passwordMismatch", true);
            return "redirect:/users/register";
        }
        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userData", bindingResult);
            return "redirect:/users/register";
        }
        if (!userService.register(data)) {
            redirectAttributes.addFlashAttribute("registrationFailed", true);
            return "redirect:/users/register";
            }
        redirectAttributes.addFlashAttribute("successfulRegistration", true);
//        emailService.sendSimpleEmail(data.getEmail(), "Успешна регистрация", "Вие се регистрирахте успешно в апликацията на 'Екслкузив сервиз'. Може да добавите своя автомобил и да запишете час за него. До скоро!");
//        emailService.sendSimpleEmail("exclautoservice@gmail.com", "Нова регистрация.", String.format("Потребител с име %s и %s се регистрира в приложението.", data.getName(), data. getEmail()));
        return "redirect:/users/login";
    }
    @GetMapping("/{id}")
    public String viewUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()){
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        User user = userOptional.get();
        if (!user.getId().equals(userService.findLoggedUser().getId()) && !userService.loggedUserHasRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("noPrivilegeMessage", true);
            return "redirect:/error/contact-admin";
        }
        UserViewDTO data = modelMapper.map(user, UserViewDTO.class);

        model.addAttribute("userViewData", data);
        model.addAttribute("id", id);
        return "view-user";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        model.addAttribute("userData", user);
        model.addAttribute("id", id);
        model.addAttribute("isEdit", true);
        return "form-user";
    }
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @Valid UserEditDTO user,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes, Model model) {
        
        if (!userService.findLoggedUser().getId().equals(id)) {
            if (!userService.loggedUserHasRole("ADMIN")){
                redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
                return "redirect:/error/contact-admin";
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("userData", user);
            model.addAttribute("org.springframework.validation.BindingResult.userData", bindingResult);
            model.addAttribute("isEdit", true);
            return "form-user";
        }
        boolean success = userService.updateUser(id, user);
        if (!success){
            return "redirect:/users/edit/" + id;
        }
        return "redirect:/";
    }
    @PostMapping("/add-admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String addAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        User user = optionalUser.get();
        if (userService.isAdmin(user.getRoles())) {
            return "redirect:/users/" + id;
        }
        userService.addAdmin(id);
        redirectAttributes.addFlashAttribute("addAdminMessage", true);
        return "redirect:/users/" + id;
    }
    @PostMapping("/remove-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeAdmin(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        User user = optionalUser.get();
        if (!userService.isAdmin(user.getRoles())) {
            return "redirect:/users/" + id;
        }
        boolean success = userService.removeAdmin(id);
        if (success) {
            redirectAttributes.addFlashAttribute("removeAdminMessage", true);
        }
        return "redirect:/users/" + id;
    }
}
