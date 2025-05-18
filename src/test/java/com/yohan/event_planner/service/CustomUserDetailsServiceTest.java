package com.yohan.event_planner.service;

import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        // Arrange
        User testUser = new User(
                "testuser",
                new PasswordVO("$2a$10$hashedPasswordPlaceholder"), // hashed password placeholder
                "test@example.com",
                ZoneId.of("UTC"),
                "Test",
                "User"
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        var userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        // Add more assertions as needed for roles/authorities if you add them later
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findByUsername("missinguser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missinguser"));
    }
}
