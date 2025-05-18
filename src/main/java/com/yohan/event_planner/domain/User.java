package com.yohan.event_planner.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.yohan.event_planner.validation.ValidZoneId;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Entity representing a user of the event planner application.
 * <p>
 * Includes fields for authentication, profile info, timezone, and audit timestamps.
 * Enforces validation constraints for fields using Jakarta Validation annotations.
 * <p>
 * The {@code createdDate} and {@code updatedDate} fields are automatically set before
 * persistence and update operations, reflecting the current time in the user's timezone.
 * <p>
 * The {@code enabled} flag indicates whether the user account is active.
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username identifying the user.
     * Must be between 3 and 30 characters, non-blank.
     */
    @NotBlank(message = "Username can not be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Hashed password for authentication.
     * Stored as a hash, between 8 and 72 characters.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    @Column(nullable = false)
    private String passwordHash;

    /**
     * User's email address.
     * Must be unique, valid format, non-blank.
     */
    @NotBlank(message = "Email can not be blank")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * User's first name.
     * Non-blank, length 1 to 50.
     */
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    /**
     * User's last name.
     * Non-blank, length 1 to 50.
     */
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    /**
     * Timestamp when the user was created.
     * Set automatically and immutable after creation.
     */
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    /**
     * Timestamp when the user was last updated.
     * Updated automatically on each update.
     */
    @Column(nullable = false)
    private ZonedDateTime updatedDate;

    /**
     * Flag indicating if the user is active/enabled.
     * Defaults to true.
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * User's timezone.
     * Used to localize timestamps and event times.
     * Validated with custom {@link com.yohan.event_planner.validation.ValidZoneId} annotation.
     */
    @NotNull(message = "Timezone is required")
    @ValidZoneId(message = "Invalid timezone provided")
    @Column(nullable = false)
    private ZoneId timezone;

    // Default constructor required by JPA
    public User() {
    }

    /**
     * Constructor for creating a new User instance with required fields.
     *
     * @param username     unique username
     * @param passwordHash hashed password
     * @param email        unique email address
     * @param timezone     user timezone
     * @param firstName    first name
     * @param lastName     last name
     */
    public User(String username, String passwordHash, String email, ZoneId timezone, String firstName, String lastName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.timezone = timezone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ZoneId getTimezone() {
        return timezone;
    }

    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }

    // --- Lifecycle callbacks for audit timestamps ---

    /**
     * Sets createdDate and updatedDate before the entity is persisted,
     * using the current time in the user's timezone.
     * Also ensures enabled flag is true by default.
     */
    @PrePersist
    public void prePersist() {
        ZonedDateTime now = getCurrentTimeInUserZone();
        createdDate = now;
        updatedDate = now;
        enabled = true;
    }

    /**
     * Updates updatedDate before the entity is updated,
     * using the current time in the user's timezone.
     */
    @PreUpdate
    public void preUpdate() {
        updatedDate = getCurrentTimeInUserZone();
    }

    /**
     * Helper method to get the current ZonedDateTime in the user's timezone.
     *
     * @return current time localized to user's timezone
     */
    public ZonedDateTime getCurrentTimeInUserZone() {
        // Defensive: if timezone is null (should not happen), fallback to system default
        return timezone != null ? ZonedDateTime.now(timezone) : ZonedDateTime.now();
    }
}
