package com.example.controller;

import com.example.model.*;
import com.example.service.ActivityService;
import com.example.service.ResourceService;
import com.example.service.SchoolService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/user")
public class UserViewController {

	@Autowired
	private UserService userService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private SchoolService schoolService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Show registration form
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<School> schools = schoolService.getAllSchools();
		model.addAttribute("schools", schools);
		model.addAttribute("client", new UserViewModel());
		return "user/register";
	}

	@PostMapping("/register")
	public String processRegisterForm(@ModelAttribute("client") UserViewModel client, Model model) {
		// Validation checks
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

		// Check if email exists
		UserViewModel existingUser = userService.findUserByEmail(client.getEmail());
		if (existingUser != null) {
			model.addAttribute("error", "Email already exists.");
			return "user/register";
		}

		// Check if identity card number (IC) exists
		List<UserViewModel> usersWithIC = userService.findUsersByIC(client.getIdentityCardNumber());
		if (!usersWithIC.isEmpty()) {
			model.addAttribute("error", "Identity Card Number already exists.");
			return "user/register";
		}

		// Save the user with encoded password
		client.setPassword(passwordEncoder.encode(client.getPassword()));
		client.setRole(3); // Default role as student
		try {
			userService.saveUser(client);
		} catch (Exception e) {
			model.addAttribute("error", "An unexpected error occurred. Please try again.");
			e.printStackTrace();
			return "user/register";
		}

		return "redirect:/user/login?registered=true";
	}

	// Show login form
	@GetMapping("/login")
	public String showLoginForm() {
		return "user/login";
	}

	@GetMapping("/dashboard")
	public String showDashboard(Model model, Authentication authentication) {
		try {
			// Add debug logging
			System.out.println("Authentication object: " + authentication);
			System.out.println("Authentication name: " + authentication.getName());
			System.out.println("Authentication authorities: " + authentication.getAuthorities());

			UserViewModel user = userService.findUserByEmail(authentication.getName());
			if (user == null) {
				System.out.println("User not found in database");
				return "redirect:/user/login";
			}

			System.out.println("Found user: " + user.getEmail() + " with role: " + user.getRole());

			// Load all required data
			List<ActivityViewModel> programs = activityService.getAllActivities();
			List<School> schools = schoolService.getAllSchools();
			List<Resource> resources = resourceService.getAllResources();

			// Add to model
			model.addAttribute("client", user);
			model.addAttribute("programs", programs);
			model.addAttribute("schools", schools);
			model.addAttribute("resources", resources);

			return "user/dashboard";
		} catch (Exception e) {
			System.out.println("Error in dashboard: " + e.getMessage());
			e.printStackTrace();
			return "redirect:/user/login?error=true";
		}
	}

	// Show profile
	@GetMapping("/profile")
	public String showProfile(Model model, Authentication authentication) {
		UserViewModel user = userService.findUserByEmail(authentication.getName());
		if (user == null) {
			model.addAttribute("error", "You must log in to view the profile.");
			return "redirect:/user/login";
		}
		model.addAttribute("client", user);
		return "user/profile";
	}

	@GetMapping("/updateProfile")
	public String showUpdateProfile(Model model, Authentication authentication) {
		UserViewModel user = userService.findUserByEmail(authentication.getName());
		if (user == null) {
			return "redirect:/user/login";
		}

		List<School> schoolList = schoolService.getAllSchools(); // Retrieve school list from DB
		model.addAttribute("client", user);
		model.addAttribute("schools", schoolList); // Pass school list to the view
		return "user/updateProfile";
	}

	@PostMapping("/updateProfile")
	public String updateProfile(@ModelAttribute("client") UserViewModel updatedClient, Authentication authentication,
			Model model) {
		UserViewModel loggedInUser = userService.findUserByEmail(authentication.getName());
		if (loggedInUser == null) {
			model.addAttribute("error", "You must log in to update your profile.");
			return "redirect:/user/login";
		}

		// Update the logged-in user's details
		loggedInUser.setFullName(updatedClient.getFullName());
		loggedInUser.setEmail(updatedClient.getEmail());
		loggedInUser.setSchool(updatedClient.getSchool());
		loggedInUser.setIdentityCardNumber(updatedClient.getIdentityCardNumber());

		String newPassword = updatedClient.getPassword();
		if (newPassword != null && !newPassword.isEmpty()) {
			loggedInUser.setPassword(newPassword);
		}

		// Save updated user details to the database
		userService.updateUser(loggedInUser);

		// Redirect to profile page with a success message
		return "redirect:/user/profile?message=Profile+updated+successfully";
	}

	// Show user list
	@PreAuthorize("hasRole('ROLE_2')")
	@GetMapping("/userList")
	public String showUserList(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "role", required = false) Integer role,
			@RequestParam(value = "page", defaultValue = "1") int currentPage,
			Model model, Authentication authentication) {

		// Get the current user's school
		UserViewModel user = userService.findUserByEmail(authentication.getName());

		// Apply filters based on user input
		List<UserViewModel> userList;
		if (name != null || email != null || role != null) {
			userList = userService.findUsersByFilter(user.getSchool(), name, email, role);
		} else {
			userList = userService.findUsersBySchool(user.getSchool());
		}

		// Pagination logic
		int pageSize = 10; // Number of users per page
		int totalItems = userList.size();
		int totalPages = (int) Math.ceil((double) totalItems / pageSize);

		// Validate current page
		if (currentPage < 1)
			currentPage = 1;
		if (currentPage > totalPages)
			currentPage = totalPages > 0 ? totalPages : 1;

		// Calculate the start and end indices for pagination
		int start = (currentPage - 1) * pageSize;
		int end = Math.min(start + pageSize, totalItems);

		// Ensure the list isn't empty before sublisting
		List<UserViewModel> paginatedUserList = totalItems > 0 ? userList.subList(start, end) : new ArrayList<>();

		// Add attributes to the model for view rendering
		model.addAttribute("userList", paginatedUserList);
		model.addAttribute("schoolName", user.getSchool());
		model.addAttribute("filterName", name);
		model.addAttribute("filterEmail", email);
		model.addAttribute("filterRole", role);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);

		return "user/userList";
	}

	@PreAuthorize("hasRole('ROLE_2')")
	@GetMapping("/createUser")
	public String showCreateUser(Model model, Authentication authentication) {
		UserViewModel teacher = userService.findUserByEmail(authentication.getName());
		UserViewModel newUser = new UserViewModel();
		newUser.setSchool(teacher.getSchool());
		model.addAttribute("client", newUser);
		return "user/createUser";
	}

	@PreAuthorize("hasRole('ROLE_2')")
	@PostMapping("/createUser")
	public String processCreateUser(@ModelAttribute("client") UserViewModel client, Model model,
			Authentication authentication) {
		UserViewModel teacher = userService.findUserByEmail(authentication.getName());

		// Validate school matches teacher's school
		if (!client.getSchool().equals(teacher.getSchool())) {
			model.addAttribute("error", "The user's school must match your school.");
			return "user/createUser";
		}

		// Check if email exists
		if (userService.findUserByEmail(client.getEmail()) != null) {
			model.addAttribute("error", "Email already exists.");
			return "user/createUser";
		}

		// Save new user
		client.setPassword(passwordEncoder.encode(client.getPassword()));
		client.setRole(3); // Default role as student
		userService.saveUser(client);

		return "redirect:/user/userList";
	}

	// Show edit user form
	@PreAuthorize("hasRole('ROLE_2')")
	@GetMapping("/editUser/{id}")
	public String showEditUser(@PathVariable("id") Long id, Model model, Authentication authentication) {
		// Get logged-in user using Authentication object
		String email = authentication.getName();
		UserViewModel loggedInClient = userService.findUserByEmail(email);

		// Fetch the user by id
		UserViewModel user = userService.findUserById(id);
		if (user != null) {
			// Ensure the user is part of the same school as the logged-in user
			if (!user.getSchool().equals(loggedInClient.getSchool())) {
				model.addAttribute("error", "You are not authorized to edit this user.");
				return "redirect:/user/userList";
			}
			model.addAttribute("user", user);
		} else {
			model.addAttribute("error", "User not found.");
			return "redirect:/user/userList";
		}

		return "user/editUser"; // JSP page for displaying the user edit form
	}

	@PreAuthorize("hasRole('ROLE_2')")
	@PostMapping("/editUser/{id}")
	public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") UserViewModel updatedUser,
			Authentication authentication, Model model) {
		// Get logged-in user using Authentication object
		String email = authentication.getName();
		UserViewModel loggedInClient = userService.findUserByEmail(email);

		// Ensure the updated school matches the logged-in user's school
		updatedUser.setSchool(loggedInClient.getSchool());

		// Save the user
		userService.updateUser(updatedUser);

		return "redirect:/user/userList"; // Redirect back to the user list page after update
	}

	// Delete user by ID
	@PreAuthorize("hasRole('ROLE_2')")
	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable("id") Long id, Authentication authentication, Model model) {
		// Get logged-in user using Authentication object
		String email = authentication.getName();
		UserViewModel loggedInClient = userService.findUserByEmail(email);

		// Check if the user exists
		UserViewModel user = userService.findUserById(id);
		if (user == null) {
			model.addAttribute("error", "User not found.");
			return "redirect:/user/userList";
		}

		// Ensure the user belongs to the same school as the logged-in user
		if (!user.getSchool().equals(loggedInClient.getSchool())) {
			model.addAttribute("error", "You are not authorized to delete this user.");
			return "redirect:/user/userList";
		}

		// Delete the user
		userService.deleteUserById(id);

		// Redirect to user list with success message
		model.addAttribute("success", "User deleted successfully.");
		return "redirect:/user/userList";
	}

	// Logout function
	@GetMapping("/logout")
	public String logout() {
		// Spring Security handles the actual logout
		return "redirect:/user/login";
	}

}
