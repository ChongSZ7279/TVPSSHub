package com.example.controller;

import com.example.model.UserViewModel;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_2')")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/userList")
    public String showUserList(@ModelAttribute UserListFilter filter,
            Model model, Authentication authentication) {
        UserViewModel currentUser = findLoggedInUser(authentication);
        List<UserViewModel> users = findUsers(currentUser.getSchool(), filter);
        addUserListAttributes(model, currentUser, users, filter);
        return "user/userList";
    }

    private List<UserViewModel> findUsers(String school, UserListFilter filter) {
        if (filter.hasFilters()) {
            return userService.findUsersByFilter(
                    school, filter.getName(), filter.getEmail(), filter.getRole());
        }
        return userService.findUsersBySchool(school);
    }

    private void addUserListAttributes(Model model, UserViewModel currentUser,
            List<UserViewModel> users, UserListFilter filter) {
        int currentPage = filter.getPage();
        int pageSize = 10;
        int totalItems = users.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages) currentPage = totalPages > 0 ? totalPages : 1;
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<UserViewModel> paginatedUserList = totalItems > 0
                ? users.subList(start, end) : new ArrayList<>();

        model.addAttribute("userList", paginatedUserList);
        model.addAttribute("schoolName", currentUser.getSchool());
        model.addAttribute("filterName", filter.getName());
        model.addAttribute("filterEmail", filter.getEmail());
        model.addAttribute("filterRole", filter.getRole());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
    }

    private UserViewModel findLoggedInUser(Authentication authentication) {
        return userService.findUserByEmail(authentication.getName());
    }

    @GetMapping("/createUser")
    public String showCreateUser(Model model, Authentication authentication) {
        UserViewModel teacher = userService.findUserByEmail(authentication.getName());
        UserViewModel newUser = new UserViewModel();
        newUser.setSchool(teacher.getSchool());
        model.addAttribute("client", newUser);
        return "user/createUser";
    }

    @PostMapping("/createUser")
    public String processCreateUser(@ModelAttribute("client") UserViewModel client, Model model,
            Authentication authentication) {
        UserViewModel teacher = userService.findUserByEmail(authentication.getName());
        if (!client.getSchool().equals(teacher.getSchool())) {
            model.addAttribute("error", "The user's school must match your school.");
            return "user/createUser";
        }
        if (userService.findUserByEmail(client.getEmail()) != null) {
            model.addAttribute("error", "Email already exists.");
            return "user/createUser";
        }
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(3);
        userService.saveUser(client);
        return "redirect:/user/userList";
    }

    @GetMapping("/editUser/{id}")
    public String showEditUser(@PathVariable("id") Long id, Model model, Authentication authentication) {
        UserViewModel loggedInClient = userService.findUserByEmail(authentication.getName());
        UserViewModel user = userService.findUserById(id);
        if (user != null) {
            if (!user.getSchool().equals(loggedInClient.getSchool())) {
                model.addAttribute("error", "You are not authorized to edit this user.");
                return "redirect:/user/userList";
            }
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "User not found.");
            return "redirect:/user/userList";
        }
        return "user/editUser";
    }

    @PostMapping("/editUser/{id}")
    public String updateUser(@PathVariable("id") Long id,
            @ModelAttribute("user") UserViewModel updatedUser,
            Authentication authentication, Model model) {
        UserViewModel loggedInClient = userService.findUserByEmail(authentication.getName());
        updatedUser.setSchool(loggedInClient.getSchool());
        userService.updateUser(updatedUser);
        return "redirect:/user/userList";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id, Authentication authentication, Model model) {
        UserViewModel loggedInClient = userService.findUserByEmail(authentication.getName());
        UserViewModel user = userService.findUserById(id);
        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "redirect:/user/userList";
        }
        if (!user.getSchool().equals(loggedInClient.getSchool())) {
            model.addAttribute("error", "You are not authorized to delete this user.");
            return "redirect:/user/userList";
        }
        userService.deleteUserById(id);
        model.addAttribute("success", "User deleted successfully.");
        return "redirect:/user/userList";
    }
}
