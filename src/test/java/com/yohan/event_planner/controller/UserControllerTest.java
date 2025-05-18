package com.yohan.event_planner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.dto.UserUpdateDTO;
import com.yohan.event_planner.exception.UserNotFoundException;
import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.service.RoleService;
import com.yohan.event_planner.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private RoleService roleService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        roleService = mock(RoleService.class);
        UserController userController = new UserController(userService, roleService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new com.yohan.event_planner.exception.GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    // Helper methods to create JSON strings for DTOs
    private String validUserCreateJson() throws Exception {
        UserCreateDTO dto = new UserCreateDTO(
                "validUser",
                "password123",
                "valid@example.com",
                "John",
                "Doe",
                "America/New_York"
        );
        return objectMapper.writeValueAsString(dto);
    }

    private String validUserUpdateJson() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO(
                "updatedUser",
                "newpassword123",
                "updated@example.com",
                "Jane",
                "Smith",
                "Europe/London"
        );
        return objectMapper.writeValueAsString(dto);
    }

    private String userResponseJson(Long id, String username, String email, String firstName, String lastName, String timezone) throws Exception {
        UserResponseDTO dto = new UserResponseDTO(id, username, email, firstName, lastName, timezone);
        return objectMapper.writeValueAsString(dto);
    }

    @Test
    void createUser_ValidInput_ReturnsCreatedUser() throws Exception {
        // Arrange
        Role roleUser = new Role("ROLE_USER");
        when(roleService.getRoleByName("ROLE_USER")).thenReturn(Optional.of(roleUser));

        UserResponseDTO responseDTO = new UserResponseDTO(1L, "validUser", "valid@example.com", "John", "Doe", "America/New_York");
        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(content().json(userResponseJson(1L, "validUser", "valid@example.com", "John", "Doe", "America/New_York")));

        verify(roleService).getRoleByName("ROLE_USER");
        verify(userService).createUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_InvalidInput_ReturnsBadRequest() throws Exception {
        // Empty JSON, missing required fields
        String invalidJson = "{}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreateDTO.class));
        verify(roleService, never()).getRoleByName(any());
    }

    @Test
    void getUser_ExistingId_ReturnsUser() throws Exception {
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "validUser", "valid@example.com", "John", "Doe", "America/New_York");
        when(userService.getUserById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(userResponseJson(1L, "validUser", "valid@example.com", "John", "Doe", "America/New_York")));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUser_NotFound_ReturnsNotFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"));

        verify(userService).getUserById(999L);
    }

    @Test
    void updateUser_ValidInput_ReturnsUpdatedUser() throws Exception {
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "updatedUser", "updated@example.com", "Jane", "Smith", "Europe/London");
        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserUpdateJson()))
                .andExpect(status().isOk())
                .andExpect(content().json(userResponseJson(1L, "updatedUser", "updated@example.com", "Jane", "Smith", "Europe/London")));

        verify(userService).updateUser(eq(1L), any(UserUpdateDTO.class));
    }

    @Test
    void updateUser_NotFound_ReturnsNotFound() throws Exception {
        when(userService.updateUser(eq(999L), any(UserUpdateDTO.class)))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(patch("/api/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserUpdateJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"));

        verify(userService).updateUser(eq(999L), any(UserUpdateDTO.class));
    }

    @Test
    void deleteUser_ExistingId_ReturnsNoContent() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ReturnsNotFound() throws Exception {
        doThrow(new UserNotFoundException(999L)).when(userService).deleteById(999L);

        mockMvc.perform(delete("/api/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with ID 999 not found"));

        verify(userService).deleteById(999L);
    }
}
