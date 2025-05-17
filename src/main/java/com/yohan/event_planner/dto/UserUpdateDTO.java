package com.yohan.event_planner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import com.yohan.event_planner.validation.ValidZoneId;

/**
 * Data Transfer Object used for partial updates to an existing User.
 * All fields are optional; only non-null fields will be applied as updates.
 *
 * Validation constraints:
 * - username, if provided, must be between 3 and 30 characters.
 * - password, if provided, must be between 8 and 72 characters.
 * - email, if provided, must be a valid email format.
 * - firstName, if provided, must be between 1 and 50 characters.
 * - lastName, if provided, must be between 1 and 50 characters.
 * - timezone, if provided, must be a valid timezone ID.
 *
 * @param username  optional new username
 * @param password  optional new password
 * @param email     optional new email address
 * @param firstName optional new first name
 * @param lastName  optional new last name
 * @param timezone  optional new timezone ID string
 */
public record UserUpdateDTO(
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        String username,

        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password,

        @Email(message = "Invalid email format")
        String email,

        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        String firstName,

        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        String lastName,

        @ValidZoneId(message = "Invalid timezone provided")
        String timezone
) {}
