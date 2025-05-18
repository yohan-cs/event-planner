package com.yohan.event_planner.controller;

import com.yohan.event_planner.dto.UserLoginDTO;
import com.yohan.event_planner.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsible for user authentication endpoints.
 *
 * <p>
 * This controller exposes a login endpoint where users provide their credentials.
 * Upon successful authentication, it generates a JWT token and returns it in the response.
 * </p>
 *
 * <p>
 * The controller leverages Spring Security's AuthenticationManager to authenticate credentials
 * and JwtService to generate JWT tokens.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructs the AuthenticationController with required dependencies.
     *
     * @param authenticationManager the Spring Security AuthenticationManager to perform authentication
     * @param jwtService the service responsible for generating and validating JWT tokens
     */
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Handles login requests by validating user credentials and returning a JWT token.
     *
     * <p>Accepts a {@link UserLoginDTO} containing username and password,
     * authenticates the user, generates a JWT token if successful,
     * and returns the token in a JSON map.</p>
     *
     * @param loginDTO the login data transfer object containing username and password
     * @return ResponseEntity containing a JSON object with the JWT token under the key "token"
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        // Authenticate the username and password using AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        // Set the authenticated principal in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Extract the authenticated username from UserDetails
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Generate JWT token using the username
        String jwtToken = jwtService.generateToken(username);

        // Return the token in a JSON response
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }
}
