package com.yohan.event_planner.controller;

import com.yohan.event_planner.dto.UserLoginDTO;
import com.yohan.event_planner.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private AuthenticationController authenticationController;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        authenticationController = new AuthenticationController(authenticationManager, jwtService);

        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void login_success_returnsJwtToken() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        UserLoginDTO loginDTO = new UserLoginDTO(username, password);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        String expectedToken = "jwt-token-123";
        when(jwtService.generateToken(username)).thenReturn(expectedToken);

        // Act
        ResponseEntity<Map<String, String>> response = authenticationController.login(loginDTO);

        // Assert
        // Verify AuthenticationManager.authenticate called with correct UsernamePasswordAuthenticationToken
        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());
        UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
        assertEquals(username, capturedToken.getPrincipal());
        assertEquals(password, capturedToken.getCredentials());

        // Verify SecurityContextHolder context set
        verify(securityContext).setAuthentication(authentication);

        // Verify JWT token generated using username
        verify(jwtService).generateToken(username);

        // Assert response contains expected token
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("token"));
        assertEquals(expectedToken, response.getBody().get("token"));
    }

    @Test
    void login_authenticationFails_throwsException() {
        // Arrange
        String username = "failuser";
        String password = "wrongpass";
        UserLoginDTO loginDTO = new UserLoginDTO(username, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            authenticationController.login(loginDTO);
        });

        assertEquals("Bad credentials", thrown.getMessage());
    }
}
