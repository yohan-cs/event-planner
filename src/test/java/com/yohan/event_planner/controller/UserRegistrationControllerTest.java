package com.yohan.event_planner.controller;

import com.yohan.event_planner.domain.Role;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserRegistrationResponseDTO;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.RoleNotFoundException;
import com.yohan.event_planner.exception.UsernameException;
import com.yohan.event_planner.repository.RoleRepository;
import com.yohan.event_planner.repository.UserRepository;
import com.yohan.event_planner.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRegistrationControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private UserCreateDTO validUserCreateDTO() {
        return new UserCreateDTO(
                "newuser",           // username
                "StrongPassword123!",// password
                "newuser@example.com", // email
                "New",               // firstName
                "User",              // lastName
                "America/New_York"   // timezone
        );
    }

    @Test
    void registerUser_shouldCreateUserSuccessfully_whenValidData() {
        UserCreateDTO dto = validUserCreateDTO();

        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        Role userRole = new Role("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode(dto.password())).thenReturn("hashed-password");

        ResponseEntity<?> response = controller.registerUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserRegistrationResponseDTO);

        assertTrue(response.getBody() instanceof UserRegistrationResponseDTO);

        UserRegistrationResponseDTO responseBody = (UserRegistrationResponseDTO) response.getBody();

        assertEquals("User registered successfully", responseBody.message());
        assertEquals("newuser", responseBody.user().username());
        assertEquals("newuser@example.com", responseBody.user().email());
        assertEquals("New", responseBody.user().firstName());
        assertEquals("User", responseBody.user().lastName());
        assertEquals("America/New_York", responseBody.user().timezone());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(dto.username(), savedUser.getUsername());
        assertEquals(dto.email(), savedUser.getEmail());
        assertEquals(ZoneId.of(dto.timezone()), savedUser.getTimezone());
        assertEquals(dto.firstName(), savedUser.getFirstName());
        assertEquals(dto.lastName(), savedUser.getLastName());
        assertTrue(savedUser.getRoles().contains(userRole));

        // Password should be hashed and wrapped in PasswordVO, check hashed value
        assertEquals("hashed-password", savedUser.getPasswordHash());
    }

    @Test
    void registerUser_shouldThrowUsernameException_whenUsernameExists() {
        UserCreateDTO dto = validUserCreateDTO();

        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.of(TestConstants.TEST_USER));

        UsernameException ex = assertThrows(UsernameException.class, () -> controller.registerUser(dto));
        assertEquals(dto.username(), ex.getMessage().contains(dto.username()) ? dto.username() : "");
    }

    @Test
    void registerUser_shouldThrowEmailException_whenEmailExists() {
        UserCreateDTO dto = validUserCreateDTO();

        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(TestConstants.TEST_USER));

        EmailException ex = assertThrows(EmailException.class, () -> controller.registerUser(dto));
        assertEquals(dto.email(), ex.getMessage().contains(dto.email()) ? dto.email() : "");
    }

    @Test
    void registerUser_shouldThrowRoleNotFoundException_whenDefaultRoleMissing() {
        UserCreateDTO dto = validUserCreateDTO();

        when(userRepository.findByUsername(dto.username())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        // Mock password encoding properly to avoid password-related exceptions
        when(passwordEncoder.encode(dto.password())).thenReturn("hashed-password");

        // Simulate missing role to trigger RoleNotFoundException
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        RoleNotFoundException ex = assertThrows(RoleNotFoundException.class, () -> controller.registerUser(dto));
        assertTrue(ex.getMessage().contains("ROLE_USER"));
    }
}
