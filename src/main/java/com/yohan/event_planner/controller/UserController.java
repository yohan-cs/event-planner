package com.yohan.event_planner.controller;

import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yohan.event_planner.service.UserService;

/**
 * REST controller for managing user-related operations such as creation,
 * retrieval, update, and deletion of users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructor for dependency injection of UserService.
     * @param userService the service handling user business logic
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user with the provided user details.
     *
     * @param userCreateDTO validated DTO containing user creation details
     * @return ResponseEntity with the created UserResponseDTO and HTTP status 201 Created
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO createdUser = userService.createUser(userCreateDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return ResponseEntity with the UserResponseDTO and HTTP status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates an existing user partially.
     *
     * @param id the ID of the user to update
     * @param updateDTO validated DTO containing fields to update
     * @return ResponseEntity with the updated UserResponseDTO and HTTP status 200 OK
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO
    ) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return ResponseEntity with HTTP status 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
