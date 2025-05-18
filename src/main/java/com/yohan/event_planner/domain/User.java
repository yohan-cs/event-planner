package com.yohan.event_planner.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.yohan.event_planner.validation.ValidZoneId;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user of the event planner application.
 *
 * Includes fields for authentication, profile info, timezone, audit timestamps,
 * and user roles for authorization.
 *
 * Passwords are encapsulated in the {@link PasswordVO} value object to manage hashing
 * and validation securely. The hashed password string is persisted in the database.
 *
 * The {@code createdDate} and {@code updatedDate} fields are automatically set before
 * persistence and update operations, reflecting the current time in the user's timezone.
 *
 * The {@code enabled} flag indicates whether the user account is active.
 *
 * The {@code roles} field establishes a bidirectional many-to-many relationship with
 * {@link Role}, representing the security roles assigned to the user.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username can not be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "Email can not be blank")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    @Column(nullable = false)
    private ZonedDateTime updatedDate;

    @Column(nullable = false)
    private boolean enabled = true;

    @NotNull(message = "Timezone is required")
    @Column(nullable = false)
    private ZoneId timezone;

    @Transient
    private PasswordVO passwordVO;

    /**
     * Bidirectional many-to-many relationship to {@link Role}.
     * <p>
     * Roles are eagerly fetched with the user for immediate availability during authorization.
     * The join table 'user_roles' stores the mapping between user IDs and role IDs.
     * <p>
     * Helper methods {@code addRole} and {@code removeRole} maintain consistency
     * on both sides of the association.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Constructors, existing getters and setters remain unchanged...

    public User() {
    }

    public User(String username, PasswordVO passwordVO, String email, ZoneId timezone, String firstName, String lastName) {
        this.username = username;
        this.setPasswordVO(passwordVO);
        this.email = email;
        this.timezone = timezone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // --- Roles related methods ---

    /**
     * Gets the roles assigned to this user.
     *
     * @return a set of roles associated with the user
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the roles assigned to this user.
     * <p>
     * This replaces the entire role set and updates both sides of the
     * bidirectional association to maintain consistency.
     *
     * @param newRoles the new set of roles to assign
     */
    public void setRoles(Set<Role> newRoles) {
        for (Role role : this.roles) {
            role.getUsers().remove(this);
        }

        this.roles = new HashSet<>();

        if (newRoles != null) {
            for (Role role : newRoles) {
                this.roles.add(role);
                role.getUsers().add(this);
            }
        }
    }


    /**
     * Adds a role to the user and synchronizes the bidirectional association.
     *
     * @param role the role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    /**
     * Removes a role from the user and synchronizes the bidirectional association.
     *
     * @param role the role to remove
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    // --- Existing getters/setters for other fields ---

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
        this.passwordVO = new PasswordVO(passwordHash);
    }

    public PasswordVO getPasswordVO() {
        if (passwordVO == null && passwordHash != null) {
            passwordVO = new PasswordVO(passwordHash);
        }
        return passwordVO;
    }

    public void setPasswordVO(PasswordVO passwordVO) {
        this.passwordVO = passwordVO;
        this.passwordHash = passwordVO.getHashedPassword();
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

    @PrePersist
    public void prePersist() {
        ZonedDateTime now = getCurrentTimeInUserZone();
        createdDate = now;
        updatedDate = now;
        enabled = true;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = getCurrentTimeInUserZone();
    }

    public ZonedDateTime getCurrentTimeInUserZone() {
        return timezone != null ? ZonedDateTime.now(timezone) : ZonedDateTime.now();
    }
}
