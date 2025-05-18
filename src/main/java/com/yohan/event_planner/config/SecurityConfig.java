package com.yohan.event_planner.config;

import com.yohan.event_planner.security.JwtAuthFilter;
import com.yohan.event_planner.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 *
 * <p>
 * Sets up JWT-based authentication and authorization including:
 * <ul>
 *   <li>Disabling CSRF protection (stateless API)</li>
 *   <li>Configuring stateless session management</li>
 *   <li>Allowing unauthenticated access to authentication endpoints</li>
 *   <li>Securing all other endpoints</li>
 *   <li>Registering the custom UserDetailsService</li>
 *   <li>Registering the JWT Authentication Filter</li>
 *   <li>Enabling method-level security annotations like @PreAuthorize</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Constructs the security configuration with necessary dependencies.
     *
     * @param customUserDetailsService service to load user details from the database
     * @param jwtAuthFilter the filter to validate JWT tokens on each request
     */
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Defines the password encoder bean using BCrypt hashing algorithm.
     *
     * @return BCryptPasswordEncoder instance for password hashing and verification
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the authentication manager bean to be used for authenticating login credentials.
     *
     * @param authConfig Spring's authentication configuration
     * @return an AuthenticationManager instance
     * @throws Exception if the manager cannot be built
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines the main security filter chain for HTTP requests.
     *
     * <p>This configuration includes:
     * <ul>
     *   <li>CSRF disabled for stateless JWT security</li>
     *   <li>Stateless session management</li>
     *   <li>Public access to authentication endpoints under /api/auth/**</li>
     *   <li>Authentication required for all other endpoints</li>
     *   <li>Registers the JWT authentication filter to run before UsernamePasswordAuthenticationFilter</li>
     * </ul>
     * </p>
     *
     * @param http HttpSecurity instance provided by Spring Security
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection (stateless API)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Public endpoints like login and register
                        .anyRequest().authenticated()                 // All other endpoints require authentication
                )
                .userDetailsService(customUserDetailsService)   // Use custom UserDetailsService
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

        return http.build();
    }
}
