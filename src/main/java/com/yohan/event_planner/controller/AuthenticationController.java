package com.yohan.event_planner.controller;

import com.yohan.event_planner.dto.UserLoginDTO;
import com.yohan.event_planner.security.JwtService;
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

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Handles user login requests. Authenticates user credentials,
     * generates a JWT token on successful authentication, and returns it.
     *
     * @param loginDTO The user's login credentials (username and password)
     * @return A ResponseEntity containing the JWT token in a JSON map
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