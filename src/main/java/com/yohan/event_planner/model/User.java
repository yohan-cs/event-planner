package com.yohan.event_planner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.yohan.event_planner.validation.ValidZoneId;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username can not be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    @Column(nullable = false)
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
    @ValidZoneId(message = "Invalid timezone provided")
    @Column(nullable = false)
    private ZoneId timezone;

    public User() {
    }

    public User(String username, String passwordHash, String email, ZoneId timezone, String firstName, String lastName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.timezone = timezone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ZoneId getTimezone() {
        return timezone;
    }

    @PrePersist
    public void prePersist() {
        ZonedDateTime currentTimeInUserZone = getCurrentTimeInUserZone();
        createdDate = currentTimeInUserZone;
        updatedDate = currentTimeInUserZone;
        enabled = true;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = getCurrentTimeInUserZone();
    }

    public ZonedDateTime getCurrentTimeInUserZone() {
        return ZonedDateTime.now(getTimezone());
    }

}
