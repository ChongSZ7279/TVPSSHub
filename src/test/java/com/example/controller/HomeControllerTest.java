package com.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    void rootRedirectsToLoginPage() {
        HomeController controller = new HomeController();

        String viewName = controller.root();

        assertEquals("redirect:/user/login", viewName);
    }
}