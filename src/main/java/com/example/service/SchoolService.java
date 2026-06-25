package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dao.SchoolDAO;
import com.example.model.School;

@Service
public class SchoolService {

    @Autowired
    private SchoolDAO schoolDAO;

    public List<School> getAllSchools() {
        return schoolDAO.getAllSchools();
    }

    public void saveSchool(School school) {
        schoolDAO.saveSchool(school);
    }

    public School getSchoolById(int id) {
        return schoolDAO.getSchoolById(id);
    }

    public void deleteSchool(int id) {
        schoolDAO.deleteSchool(id);
    }
}
