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

    @Test
    void returnsConfiguredAuthority() {
        CustomUserDetails details = new CustomUserDetails(
                7L,
                15L,
                "teacher@example.com",
                "secret",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_2")));

        assertEquals(1, details.getAuthorities().size());
        assertEquals("ROLE_2", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void alwaysReportsActiveAccountState() {
        CustomUserDetails details = new CustomUserDetails(
                7L,
                15L,
                "teacher@example.com",
                "secret",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_2")));

        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }
}