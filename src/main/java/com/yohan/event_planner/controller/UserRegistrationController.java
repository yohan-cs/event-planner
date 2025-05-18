package com.yohan.event_planner.controller;

import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserRegistrationResponseDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.ErrorCode;
import com.yohan.event_planner.exception.RoleNotFoundException;
import com.yohan.event_planner.exception.UsernameException;
import com.yohan.event_planner.repository.RoleRepository;
import com.yohan.event_planner.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;


/**
 * Controller responsible for user registration endpoint.
 *
 * This controller handles incoming requests to create a new user account,
 * validating the input, ensuring uniqueness of username and email,
 * hashing the password, assigning default roles, and persisting the user entity.
 *
 * Example endpoint:
 * POST /api/users/register
 * with JSON body conforming to {@link UserCreateDTO}
 */
@RestController
@RequestMapping("/api/users")
public class UserRegistrationController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationController(UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     *
     * Validates the incoming user data, checks for existing username/email conflicts,
     * hashes the password, assigns the default "ROLE_USER", and saves the new user.
     *
     * Returns a JSON response containing a success message and the newly created user data
     * (excluding sensitive information like password).
     *
     * @param userCreateDTO the incoming user registration data transfer object
     * @return 201 Created with {@link UserRegistrationResponseDTO} containing success message and user info,
     *         or 409 Conflict if username/email exists.
     * @throws RoleNotFoundException if default role "ROLE_USER" is not found in the database
     */
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        if (userRepository.findByUsername(userCreateDTO.username()).isPresent()) {
            throw new UsernameException(ErrorCode.DUPLICATE_USERNAME, userCreateDTO.username());
        }

        if (userRepository.findByEmail(userCreateDTO.email()).isPresent()) {
            throw new EmailException(ErrorCode.DUPLICATE_EMAIL, userCreateDTO.email());
        }

        // Convert timezone string to ZoneId (validity ensured by @ValidZoneId)
        ZoneId timezone = ZoneId.of(userCreateDTO.timezone());

        // Hash password and wrap in PasswordVO
        String hashedPassword = passwordEncoder.encode(userCreateDTO.password());
        PasswordVO passwordVO = new PasswordVO(hashedPassword);

        // Create User entity
        User user = new User(
                userCreateDTO.username(),
                passwordVO,
                userCreateDTO.email(),
                timezone,
                userCreateDTO.firstName(),
                userCreateDTO.lastName()
        );

        // Assign default role ROLE_USER or throw RoleNotFoundException
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
        user.addRole(userRole);

        // Save new user
        userRepository.save(user);

        // Build response user DTO
        UserResponseDTO userResponse = new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getTimezone().toString()
        );

        // Return success response with message and user data
        UserRegistrationResponseDTO response = new UserRegistrationResponseDTO(
                "User registered successfully",
                userResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
