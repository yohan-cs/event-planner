package com.yohan.event_planner.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login credentials.
 * Carries username and password from the login request body.
 */
public class UserLoginDTO {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Password must not be blank")
    private String password;

    // Default constructor required for JSON deserialization
    public UserLoginDTO() {
    }

    // Constructor with all fields (optional, but useful)
    public UserLoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
