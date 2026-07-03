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
        if (client.getFullName() == null || client.getFullName().isEmpty()) {
            model.addAttribute("error", "Full Name is required.");
            return "user/register";
        }
        if (client.getEmail() == null || client.getEmail().isEmpty()) {
            model.addAttribute("error", "Email is required.");
            return "user/register";
        }
        if (client.getPassword() == null || client.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "user/register";
        }
        if (!client.getPassword().equals(client.getCheckPassword())) {
            model.addAttribute("error", "Password and Confirm Password must match.");
            return "user/register";
        }

        UserViewModel existingUser = userService.findUserByEmail(client.getEmail());
        if (existingUser != null) {
            model.addAttribute("error", "Email already exists.");
            return "user/register";
        }

        List<UserViewModel> usersWithIC = userService.findUsersByIC(client.getIdentityCardNumber());
        if (!usersWithIC.isEmpty()) {
            model.addAttribute("error", "Identity Card Number already exists.");
            return "user/register";
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(3);
        try {
            userService.saveUser(client);
        } catch (Exception exception) {
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            exception.printStackTrace();
            return "user/register";
        }
        return "redirect:/user/login?registered=true";
    }
}
