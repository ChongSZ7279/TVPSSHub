package com.example.controller;

import com.example.model.ActivityViewModel;
import com.example.model.UserViewModel;
import com.example.model.Feedback;
import com.example.service.ActivityService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Controller
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    // Show activity list - Everyone can access
    @GetMapping("/activityList")
    public String showActivityList(Model model) {
        List<ActivityViewModel> activities = activityService.getAllActivities();
        model.addAttribute("activities", activities);
        return "activity/activityList";
    }

    // Add Activity - Only Teachers can add
    @PreAuthorize("hasRole('ROLE_2')")
    @GetMapping("/addActivity")
    public String showAddActivityForm(Model model) {
        model.addAttribute("activity", new ActivityViewModel());
        return "activity/addActivity";
    }

    @PostMapping("/addActivity")
    public String processAddActivityForm(@ModelAttribute("activity") ActivityViewModel activity,
            Authentication authentication) {
        // Get current user's ID from authentication
        UserViewModel currentUser = userService.findUserByEmail(authentication.getName());
        activity.setCreatorId(currentUser.getId());

        activityService.saveActivity(activity);
        return "redirect:/activity/activityList";
    }

    // View Activity Details - Everyone can access
    @GetMapping("/activityDetails/{id}")
    public String showActivityDetails(@PathVariable("id") int id, Model model) {
        ActivityViewModel activity = activityService.findActivityById(id);
        if (activity == null) {
            model.addAttribute("error", "Activity not found.");
            return "redirect:/activity/activityList";
        }
        model.addAttribute("activity", activity);
        return "activity/activityDetails";
    }

    // Edit Activity - Only Teachers who created the activity
    @PreAuthorize("hasRole('ROLE_2')")
    @GetMapping("/editActivity/{id}")
    public String showEditActivityForm(@PathVariable("id") int id, Authentication authentication, Model model) {
        System.out.println("Principal: " + authentication.getPrincipal());

        ActivityViewModel activity = activityService.findActivityById(id);
        UserViewModel currentUser = userService.findUserByEmail(authentication.getName());

        if (activity == null) {
            model.addAttribute("error", "Activity not found.");
            return "redirect:/activity/activityList";
        }

        // Check if the logged-in user is the creator
        if (activity.getCreatorId() != currentUser.getId()) {
            model.addAttribute("error", "You are not authorized to edit this activity.");
            return "redirect:/activity/activityList";
        }

        model.addAttribute("activity", activity);
        return "activity/editActivity";
    }

    @PostMapping("/editActivity/{id}")
    public String processEditActivityForm(@PathVariable("id") int id,
            @ModelAttribute("activity") ActivityViewModel updatedActivity,
            Authentication authentication,
            Model model) {
        ActivityViewModel existingActivity = activityService.findActivityById(id);
        UserViewModel currentUser = userService.findUserByEmail(authentication.getName());

        if (existingActivity != null && existingActivity.getCreatorId() == currentUser.getId()) {
            existingActivity.updateEditableFieldsFrom(updatedActivity);
            activityService.updateActivity(existingActivity);
            model.addAttribute("message", "Activity updated successfully.");
        }
        return "redirect:/activity/activityDetails/" + id;
    }

    // Filter Activities - Everyone can access
    @GetMapping("/filterActivities")
    public String filterActivities(@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "location", required = false) String location,
            Model model) {
        List<ActivityViewModel> activities = activityService.getFilteredActivities(searchKeyword, location);
        model.addAttribute("activities", activities);
        return "activity/activityList";
    }

    // Show Feedback - Only Teachers and Admin can view
    @PreAuthorize("hasAnyRole('ROLE_1', 'ROLE_2')")
    @GetMapping("/showFeedback/{id}")
    public String showFeedback(@PathVariable("id") int activityId,
            @RequestParam(name = "page", defaultValue = "1") int currentPage,
            Model model) {

        // Fetch feedback for the activity from the DAO
        List<Feedback> feedbackList = activityService.getFeedbackByActivityId(activityId);

        // Handle the case where there are no feedbacks
        if (feedbackList == null || feedbackList.isEmpty()) {
            model.addAttribute("feedbackList", new ArrayList<>()); // Empty list
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1); // No pages if no feedback
            model.addAttribute("noFeedbackMessage", "No feedback available for this activity.");
            return "activity/showFeedback";
        }

        int pageSize = 10; // Items per page
        int totalItems = feedbackList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (currentPage < 1)
            currentPage = 1;
        if (currentPage > totalPages)
            currentPage = totalPages;

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        // Paginate feedback
        List<Feedback> paginatedFeedback = feedbackList.subList(start, end);

        // Add attributes to the model for pagination
        model.addAttribute("feedbackList", paginatedFeedback);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activityId", activityId); // Pass activityId for pagination links

        return "activity/showFeedback";
    }

    // Add Feedback - Only Admin can add feedback
    @PreAuthorize("hasRole('ROLE_1')")
    @GetMapping("/addFeedback")
    public String showAddFeedback(@RequestParam("activityId") int activityId, Model model) {
        ActivityViewModel activity = activityService.findActivityById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("Activity not found for ID: " + activityId);
        }

        model.addAttribute("activity", activity);
        model.addAttribute("currentDate", LocalDate.now());
        return "activity/addFeedback";
    }

    @PostMapping("/addFeedback")
    public String addFeedback(@RequestParam("activityId") int activityId,
            @RequestParam("feedbackText") String feedbackText,
            @RequestParam(value = "rating", required = false) Integer rating,
            Authentication authentication,
            @RequestParam("date") String date) {

        UserViewModel currentUser = userService.findUserByEmail(authentication.getName());
        LocalDate parsedDate = LocalDate.parse(date);

        Feedback feedback = new Feedback();
        feedback.setActivityId(activityId);
        feedback.setFeedbackText(feedbackText);
        feedback.setRating(rating);
        feedback.setUserId(currentUser.getId().intValue());
        feedback.setDate(parsedDate);

        activityService.saveFeedback(feedback);
        return "redirect:/activity/activityDetails/" + activityId;
    }

}
