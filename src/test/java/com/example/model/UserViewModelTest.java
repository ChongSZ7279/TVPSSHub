package com.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserViewModelTest {

    @Test
    void storesAndReturnsBasicUserFields() {
        UserViewModel user = new UserViewModel();

        user.setId(10L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("Password123!");
        user.setCheckPassword("Password123!");
        user.setSchool("Sekolah Tinggi Segamat");
        user.setIdentityCardNumber("900101-14-1234");
        user.setSchoolId(5L);
        user.setRole(3);

        assertEquals(10L, user.getId());
        assertEquals("Test User", user.getFullName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Password123!", user.getPassword());
        assertEquals("Password123!", user.getCheckPassword());
        assertEquals("Sekolah Tinggi Segamat", user.getSchool());
        assertEquals("900101-14-1234", user.getIdentityCardNumber());
        assertEquals(5L, user.getSchoolId());
        assertEquals(3, user.getRole());
    }
}