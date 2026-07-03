package com.example.controller;

import com.example.model.*;
import com.example.service.ActivityService;
import com.example.service.ResourceService;
import com.example.service.SchoolService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;

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

	// Logout function
	@GetMapping("/logout")
	public String logout() {
		// Spring Security handles the actual logout
		return "redirect:/user/login";
	}

}
