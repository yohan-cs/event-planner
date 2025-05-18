package com.yohan.event_planner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for Spring Security settings.
 *
 * Defines password encoding and configures HTTP security, including
 * CSRF protection disabling, endpoint authorization, and HTTP Basic authentication.
 *
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates and exposes a PasswordEncoder bean that uses BCrypt hashing algorithm.
     * This encoder is used to hash and verify user passwords securely.
     *
     * @return a BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the security filter chain which configures HTTP security.
     *
     * Disables CSRF protection (use with caution and only if your app is API-only).
     * Configures endpoint authorization rules.
     * Enables HTTP Basic authentication using the new lambda DSL style.
     *
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain instance
     * @throws Exception if an error occurs while configuring
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (recommended for stateless REST APIs)
                .csrf(csrf -> csrf.disable())

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll() // Public endpoints
                        .anyRequest().authenticated()                   // All other requests require authentication
                )

                // Enable HTTP Basic Authentication with default settings
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
