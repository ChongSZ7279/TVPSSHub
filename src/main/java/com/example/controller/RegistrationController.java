package com.example.controller;

import com.example.model.School;
import com.example.model.UserViewModel;
import com.example.service.SchoolService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<School> schools = schoolService.getAllSchools();
        model.addAttribute("schools", schools);
        model.addAttribute("client", new UserViewModel());
        return "user/register";
    }

    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute("client") UserViewModel client, Model model) {
        String error = validateRegistration(client);
        if (error == null) {
            error = findDuplicateError(client);
        }
        if (error != null) {
            return showError(model, error);
        }

        prepareStudentAccount(client);
        try {
            userService.saveUser(client);
        } catch (RuntimeException exception) {
            return showError(model, "An unexpected error occurred. Please try again.");
        }
        return "redirect:/user/login?registered=true";
    }

    private String validateRegistration(UserViewModel client) {
        if (client.getFullName() == null || client.getFullName().isEmpty()) {
            return "Full Name is required.";
        }
        if (client.getEmail() == null || client.getEmail().isEmpty()) {
            return "Email is required.";
        }
        if (client.getPassword() == null || client.getPassword().length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (!client.getPassword().equals(client.getCheckPassword())) {
            return "Password and Confirm Password must match.";
        }
        return null;
    }

    private String findDuplicateError(UserViewModel client) {
        if (userService.findUserByEmail(client.getEmail()) != null) {
            return "Email already exists.";
        }
        if (!userService.findUsersByIC(client.getIdentityCardNumber()).isEmpty()) {
            return "Identity Card Number already exists.";
        }
        return null;
    }

    private void prepareStudentAccount(UserViewModel client) {
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(3);
    }

    private String showError(Model model, String message) {
        model.addAttribute("error", message);
        return "user/register";
    }
}
