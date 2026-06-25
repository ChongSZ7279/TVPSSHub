package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dao.ResourceDAO;
import com.example.model.Resource;

@Service
public class ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;

    public List<Resource> getAllResources() {
        return resourceDAO.getAllResources();
    }

    public void saveResource(Resource resource) {
        resourceDAO.saveResource(resource);
    }

    public Resource getResourceById(long id) {
        return resourceDAO.getResourceById(id);
    }

    public void deleteResource(long id) {
        resourceDAO.deleteResource(id);
    }

    public List<Resource> getFilteredResources(String searchText, String state) {
        return resourceDAO.getFilteredResources(searchText, state);
    }
}
