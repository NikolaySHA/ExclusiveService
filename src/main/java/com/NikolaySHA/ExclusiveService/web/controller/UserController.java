package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.dto.userDTO.*;
import com.NikolaySHA.ExclusiveService.model.entity.PasswordResetToken;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.service.PasswordResetService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import com.NikolaySHA.ExclusiveService.service.impl.GmailSender;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final GmailSender emailService;
    private final PasswordResetService passwordResetService;
    
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
                                 RedirectAttributes redirectAttributes) throws MessagingException, GeneralSecurityException, IOException {
        
        if (!data.getPassword().equals(data.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("userData", data);
            redirectAttributes.addFlashAttribute("passwordMismatch", true);
            return "redirect:/users/register";
        }
        if (bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userData", data);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userData", bindingResult);
            return "redirect:/users/register";
        }
        if (!userService.register(data)) {
            redirectAttributes.addFlashAttribute("userData", data);
            redirectAttributes.addFlashAttribute("registrationFailed", true);
            return "redirect:/users/register";
            }
       
       
        emailService.sendMail("Успешна регистрация", "Вие се регистрирахте успешно в апликацията на 'Екслкузив сервиз'. Може да добавите своя автомобил и да запишете час за него. До скоро!", data.getEmail());
        emailService.sendMail("Нова регистрация.", String.format("Потребител с име: %s и имейл: %s се регистрира в приложението.", data.getName(), data.getEmail()), "exclautoservice@gmail.com");
        
        redirectAttributes.addFlashAttribute("successfulRegistration", true);
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
        
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("notFoundErrorMessage", true);
            return "redirect:/error/contact-admin";
        }
        
        User existingUser = optionalUser.get();
        
        if (!userService.findLoggedUser().getId().equals(id) && !userService.loggedUserHasRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("noPrivilegeMessage", true);
            return "redirect:/error/contact-admin";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("userData", user);
            model.addAttribute("org.springframework.validation.BindingResult.userData", bindingResult);
            model.addAttribute("isEdit", true);
            return "form-user";
        }
        
        user.setEmail(existingUser.getEmail());
        
        boolean success = userService.updateUser(id, user);
        if (!success) {
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
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("forgotPasswordData", new ForgotPasswordDTO());
        return "forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(@ModelAttribute("forgotPasswordData") ForgotPasswordDTO forgotPasswordDTO,
                                        RedirectAttributes redirectAttributes) throws MessagingException, GeneralSecurityException, IOException {
        boolean emailExists = userService.sendPasswordResetLink(forgotPasswordDTO.getEmail());
        if (emailExists) {
            redirectAttributes.addFlashAttribute("successMessage", true);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", true);
        }
        return "redirect:/users/forgot-password";
    }
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam(required = false) String token, Model model) {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken(token);  // Прехвърляне на токена от URL в DTO
        model.addAttribute("resetPasswordDTO", dto);  // Добавяне на DTO в модела
        return "reset-password";
    }
    
    @PostMapping("/reset-password")
    public String handleResetPassword(@ModelAttribute ResetPasswordDTO resetPasswordDTO, Model model) {
        // Проверка дали паролите съвпадат
        if (!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            model.addAttribute("error_mismatch", true);
            return "reset-password";
        }
        
        // Възстановяване на паролата
        boolean success = passwordResetService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getPassword());
        if (!success) {
            model.addAttribute("error_token", true);
            return "reset-password";
        }
        
        // Съобщение за успешно сменена парола
        model.addAttribute("successfulPasswordChange", true);
        return "login";
    }
}
