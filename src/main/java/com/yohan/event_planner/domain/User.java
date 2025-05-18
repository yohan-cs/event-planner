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
 * Passwords are encapsulated in the {@link PasswordVO} value object to manage hashing
 * and validation securely. The hashed password string is persisted in the database.
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
     * Hashed password string stored in the database.
     * Managed internally and kept in sync with {@link PasswordVO}.
     */
    @Column(name = "password_hash", nullable = false)
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

    /**
     * Transient value object encapsulating password hash and validation logic.
     * Not persisted directly; the underlying hashed password string is persisted instead.
     */
    @Transient
    private PasswordVO passwordVO;

    /**
     * Default constructor required by JPA.
     */
    public User() {
    }

    /**
     * Constructs a new User instance with required fields.
     *
     * @param username   unique username
     * @param passwordVO password value object encapsulating hashed password
     * @param email      unique email address
     * @param timezone   user's timezone
     * @param firstName  first name
     * @param lastName   last name
     */
    public User(String username, PasswordVO passwordVO, String email, ZoneId timezone, String firstName, String lastName) {
        this.username = username;
        this.setPasswordVO(passwordVO);
        this.email = email;
        this.timezone = timezone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // --- Getters and Setters ---

    /**
     * Gets the user ID.
     *
     * @return the database-generated user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the username.
     *
     * @return username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username new username string
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the hashed password string used for persistence.
     *
     * @return hashed password string
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password string directly.
     * <p>
     * This method is primarily for JPA when loading from the database.
     * Also reconstructs the {@link PasswordVO} from the stored hash.
     *
     * @param passwordHash hashed password string
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.passwordVO = new PasswordVO(passwordHash);
    }

    /**
     * Gets the password value object for business logic.
     * <p>
     * Lazily constructs the {@link PasswordVO} from the stored hash if needed.
     *
     * @return password value object
     */
    public PasswordVO getPasswordVO() {
        if (passwordVO == null && passwordHash != null) {
            passwordVO = new PasswordVO(passwordHash);
        }
        return passwordVO;
    }

    /**
     * Sets the password using the value object.
     * <p>
     * Updates the internal hashed password string to stay in sync for persistence.
     *
     * @param passwordVO the password value object containing the hashed password
     */
    public void setPasswordVO(PasswordVO passwordVO) {
        this.passwordVO = passwordVO;
        this.passwordHash = passwordVO.getHashedPassword();
    }

    /**
     * Gets the email address.
     *
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's first name.
     *
     * @return first name string
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return last name string
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the date/time when the user was created.
     *
     * @return creation timestamp
     */
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Gets the date/time when the user was last updated.
     *
     * @return last updated timestamp
     */
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Sets the date/time when the user was last updated.
     *
     * @param updatedDate new updated timestamp
     */
    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * Checks if the user account is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of the user account.
     *
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the user's timezone.
     *
     * @return user's ZoneId
     */
    public ZoneId getTimezone() {
        return timezone;
    }

    /**
     * Sets the user's timezone.
     *
     * @param timezone new ZoneId
     */
    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }

    // --- Lifecycle callbacks for audit timestamps ---

    /**
     * Lifecycle callback to set createdDate and updatedDate before persisting.
     * Uses current time localized to the user's timezone.
     * Also sets the enabled flag to true by default.
     */
    @PrePersist
    public void prePersist() {
        ZonedDateTime now = getCurrentTimeInUserZone();
        createdDate = now;
        updatedDate = now;
        enabled = true;
    }

    /**
     * Lifecycle callback to update updatedDate before entity update.
     * Uses current time localized to the user's timezone.
     */
    @PreUpdate
    public void preUpdate() {
        updatedDate = getCurrentTimeInUserZone();
    }

    /**
     * Helper method to get the current ZonedDateTime in the user's timezone.
     * Falls back to system default if timezone is not set.
     *
     * @return current time in user's timezone or system default
     */
    public ZonedDateTime getCurrentTimeInUserZone() {
        return timezone != null ? ZonedDateTime.now(timezone) : ZonedDateTime.now();
    }
}
