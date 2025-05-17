package com.yohan.event_planner.dto;

/**
 * Data Transfer Object representing a User for responses.
 * Contains basic user profile information without sensitive fields like password.
 *
 * @param id        unique identifier of the user
 * @param username  user's login name
 * @param email     user's email address
 * @param firstName user's first name
 * @param lastName  user's last name
 * @param timezone  user's timezone ID as a String (e.g., "America/New_York")
 */
public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String timezone
) {}
