package com.yohan.event_planner.dto;

/**
 * Wrapper DTO for user registration response.
 *
 * Includes a message and the user information.
 *
 * @param message success or informational message about registration
 * @param user    user data without sensitive information
 */
public record UserRegistrationResponseDTO(
        String message,
        UserResponseDTO user
) {}