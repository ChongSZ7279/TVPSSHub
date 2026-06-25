package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dao.ActivityDAO;
import com.example.dao.FeedbackDAO;
import com.example.model.ActivityViewModel;
import com.example.model.Feedback;

@Service
public class ActivityService {

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private FeedbackDAO feedbackDAO;

    public void saveActivity(ActivityViewModel activity) {
        activityDAO.saveActivity(activity);
    }

    public ActivityViewModel findActivityById(int id) {
        return activityDAO.findActivityById(id);
    }

    public List<ActivityViewModel> getFilteredActivities(String searchKeyword, String location) {
        return activityDAO.getFilteredActivities(searchKeyword, location);
    }

    public List<ActivityViewModel> getAllActivities() {
        return activityDAO.getAllActivities();
    }

    public void updateActivity(ActivityViewModel activity) {
        activityDAO.updateActivity(activity);
    }

    public void deleteActivityById(int id) {
        activityDAO.deleteActivityById(id);
    }

    public void saveFeedback(Feedback feedback) {
        feedbackDAO.save(feedback);
    }

    public List<Feedback> getFeedbackByActivityId(int activityId) {
        return feedbackDAO.getFeedbackByActivityId(activityId);
    }
}
