package com.yohan.event_planner.controller;

import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.ErrorCode;
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
import java.util.Optional;

/**
 * Controller responsible for user registration endpoint.
 *
 * <p>This controller handles incoming requests to create a new user account,
 * validating the input, ensuring uniqueness of username and email,
 * hashing the password, assigning default roles, and persisting the user entity.</p>
 *
 * <p>Example endpoint:
 * POST /api/users/register
 * with JSON body conforming to {@link UserCreateDTO}</p>
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
     * @param userCreateDTO the incoming user registration data transfer object
     * @return 201 Created with success message, or 409 Conflict if username/email exists
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
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

        // Assign default role ROLE_USER
        Optional<Role> userRoleOpt = roleRepository.findByName("ROLE_USER");
        if (userRoleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Default role ROLE_USER not found, please seed roles");
        }
        user.addRole(userRoleOpt.get());

        // Save new user
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }
}
