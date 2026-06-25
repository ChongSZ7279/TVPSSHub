package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dao.UserDAO;
import com.example.model.UserViewModel;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public void saveUser(UserViewModel user) {
        userDAO.saveUser(user);
    }

    public List<UserViewModel> findUsersBySchool(String schoolName) {
        return userDAO.findUsersBySchool(schoolName);
    }

    public UserViewModel findUserByEmail(String email) {
        return userDAO.findUserByEmail(email);
    }

    public List<UserViewModel> findUsersByIC(String identityCardNumber) {
        return userDAO.findUsersByIC(identityCardNumber);
    }

    public List<UserViewModel> findUsersByFilter(String schoolName, String name, String email, Integer role) {
        return userDAO.findUsersByFilter(schoolName, name, email, role);
    }

    public UserViewModel findUserById(Long id) {
        return userDAO.findUserById(id);
    }

    public void deleteUserById(Long id) {
        userDAO.deleteUserById(id);
    }

    public void updateUser(UserViewModel user) {
        userDAO.updateUser(user);
    }

    public List<UserViewModel> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public UserViewModel getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }
}
