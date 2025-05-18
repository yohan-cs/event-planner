package com.yohan.event_planner.domain;

import com.yohan.event_planner.exception.ErrorCode;
import com.yohan.event_planner.exception.PasswordException;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Value Object representing a password hash.
 * <p>
 * Encapsulates password hashing and verification logic.
 * Supports construction from a raw password (which is hashed internally)
 * or from an existing hashed password (e.g., loaded from database).
 * </p>
 */
public class PasswordVO {

    private final String hashedPassword;

    /**
     * Constructs a PasswordVO by hashing the given raw password using the provided encoder.
     *
     * @param rawPassword the plain-text password to hash; must be non-null and meet length requirements
     * @param encoder     the password encoder to use for hashing
     * @throws PasswordException if the raw password is null or too short
     */
    public PasswordVO(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null) {
            throw new PasswordException(ErrorCode.NULL_PASSWORD);
        }
        if (rawPassword.length() < 8) {
            throw new PasswordException(ErrorCode.INVALID_PASSWORD_LENGTH);
        }
        this.hashedPassword = encoder.encode(rawPassword);
    }

    /**
     * Constructs a PasswordVO from an existing hashed password.
     * <p>
     * This constructor is intended for rehydrating the VO from persistent storage.
     * It does not perform validation or hashing.
     * </p>
     *
     * @param hashedPassword the already hashed password; must be non-null
     */
    public PasswordVO(String hashedPassword) {
        if (hashedPassword == null) {
            throw new PasswordException(ErrorCode.NULL_PASSWORD);
        }
        this.hashedPassword = hashedPassword;
    }

    /**
     * Verifies if the given raw password matches the stored hashed password,
     * using the provided encoder.
     *
     * @param rawPassword the plain-text password to verify; null returns false
     * @param encoder     the password encoder to use for verification
     * @return true if the raw password matches the stored hash, false otherwise
     */
    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null) {
            return false; // Prevent passing null to encoder, avoid exception
        }
        return encoder.matches(rawPassword, hashedPassword);
    }

    /**
     * Returns the hashed password string.
     *
     * @return the hashed password
     */
    public String getHashedPassword() {
        return hashedPassword;
    }
}
