package com.yohan.event_planner.security;

import com.yohan.event_planner.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts every HTTP request to validate
 * the JWT token from the Authorization header.
 *
 * <p>
 * This filter extracts the JWT token from the Authorization header, validates it,
 * loads the user details, and sets the Spring Security authentication context
 * so that the request is treated as authenticated.
 * </p>
 *
 * <p>
 * Extends {@link OncePerRequestFilter} to guarantee a single execution per request.
 * </p>
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructs the filter with required dependencies.
     *
     * @param jwtService service to handle JWT operations like validation and extracting claims
     * @param userDetailsService service to load user-specific data from the database
     */
    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filter method invoked once per HTTP request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to proceed to the next filter
     * @throws ServletException if an error occurs during filtering
     * @throws IOException if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // If no Authorization header or it does not start with "Bearer ", just continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token by removing the "Bearer " prefix
        jwt = authHeader.substring(7);

        // Extract the username from the JWT token
        username = jwtService.extractUsername(jwt);

        // Proceed if username exists and SecurityContext has no authentication yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate the token against the username extracted from UserDetails
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                // Create an authentication token and set it into the SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set additional details for the authentication token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication into the security context for downstream filters/controllers
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
