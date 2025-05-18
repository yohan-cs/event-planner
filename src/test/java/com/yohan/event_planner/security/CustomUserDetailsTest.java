package com.yohan.event_planner.security;

import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.domain.PasswordVO;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void getUsername_returnsCorrectUsername() {
        User user = new User(
                "testuser",
                new PasswordVO("hashedPass"),
                "test@example.com",
                ZoneId.of("UTC"),
                "Test",
                "User"
        );
        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("testuser", details.getUsername());
    }

    @Test
    void getPassword_returnsPasswordHash() {
        User user = new User(
                "testuser",
                new PasswordVO("hashedPass"),
                "test@example.com",
                ZoneId.of("UTC"),
                "Test",
                "User"
        );
        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("hashedPass", details.getPassword());
    }

    @Test
    void isEnabled_returnsUserEnabledFlag() {
        User user = new User(
                "testuser",
                new PasswordVO("hashedPass"),
                "test@example.com",
                ZoneId.of("UTC"),
                "Test",
                "User"
        );
        user.setEnabled(true);
        CustomUserDetails details = new CustomUserDetails(user);
        assertTrue(details.isEnabled());

        user.setEnabled(false);
        details = new CustomUserDetails(user);
        assertFalse(details.isEnabled());
    }

    @Test
    void accountStatusFlags_returnTrue() {
        User user = new User(
                "testuser",
                new PasswordVO("hashedPass"),
                "test@example.com",
                ZoneId.of("UTC"),
                "Test",
                "User"
        );
        CustomUserDetails details = new CustomUserDetails(user);

        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
    }

    @Test
    void getAuthorities_returnsEmptyList() {
        User user = new User(
                "testuser",
                new PasswordVO("hashedPass"),
                "test@example.com",
                ZoneId.of("UTC"),
                "Test",
                "User"
        );
        CustomUserDetails details = new CustomUserDetails(user);

        assertTrue(details.getAuthorities().isEmpty());
    }
}
