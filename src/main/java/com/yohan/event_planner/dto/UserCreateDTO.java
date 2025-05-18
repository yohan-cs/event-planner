package com.yohan.event_planner.dto;

import com.yohan.event_planner.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.yohan.event_planner.validation.ValidZoneId;

/**
 * Data Transfer Object for creating a new {@link User}.
 *
 * Contains required fields with validation constraints to ensure proper input.
 *
 * Validation constraints include:
 *
 *     username: required, between 3 and 30 characters
 *     password: required, between 8 and 72 characters
 *     email: required, must be valid email format
 *     firstName: required, between 1 and 50 characters
 *     lastName: required, between 1 and 50 characters
 *     timezone: required, must be a valid timezone ID (validated by {@link ValidZoneId})
 *
 *
 * @param username  the desired username for the user account
 * @param password  the user's password (should be hashed before persistence)
 * @param email     the user's email address
 * @param firstName the user's first name
 * @param lastName  the user's last name
 * @param timezone  the user's timezone ID (e.g., "America/New_York")
 */
public record UserCreateDTO(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30)
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72)
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 50)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 50)
        String lastName,

        @NotBlank(message = "Timezone is required")
        @ValidZoneId(message = "Invalid timezone provided")
        String timezone
) {}
