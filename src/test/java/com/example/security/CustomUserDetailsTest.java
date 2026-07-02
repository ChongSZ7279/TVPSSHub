package com.example.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class CustomUserDetailsTest {

    @Test
    void returnsConfiguredIdentityFields() {
        CustomUserDetails details = new CustomUserDetails(
                7L,
                15L,
                "teacher@example.com",
                "secret",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_2")));

        assertEquals(7L, details.getId());
        assertEquals(15L, details.getSchoolId());
        assertEquals("teacher@example.com", details.getUsername());
        assertEquals("secret", details.getPassword());
    }
}