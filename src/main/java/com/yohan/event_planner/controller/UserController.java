package com.yohan.event_planner.controller;

import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.dto.UserUpdateDTO;
import com.yohan.event_planner.exception.RoleNotFoundException;
import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.service.RoleService;
import com.yohan.event_planner.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for managing user-related operations such as creation,
 * retrieval, update, and deletion of users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    /**
     * Constructor for dependency injection of UserService and RoleService.
     *
     * @param userService the service handling user business logic
     * @param roleService the service handling role lookups
     */
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Creates a new user with the provided user details.
     * Automatically assigns the 'ROLE_USER' role.
     *
     * @param userCreateDTO validated DTO containing user creation details
     * @return ResponseEntity with the created UserResponseDTO and HTTP status 201 Created
     * @throws RoleNotFoundException if the 'ROLE_USER' role cannot be found
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        // Look up ROLE_USER role by name
        Optional<Role> userRole = roleService.getRoleByName("ROLE_USER");
        if (userRole.isEmpty()) {
            throw new RoleNotFoundException("ROLE_USER");
        }

        // Pass role info to service layer as needed
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
