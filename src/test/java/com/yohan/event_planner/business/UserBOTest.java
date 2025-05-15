package com.yohan.event_planner.business;

import com.yohan.event_planner.exception.UserNotFoundException;
import com.yohan.event_planner.exception.DuplicateUsernameException;
import com.yohan.event_planner.exception.DuplicateEmailException;
import com.yohan.event_planner.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.yohan.event_planner.repository.UserRepository;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserBOTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserBO userBO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsersByFirstAndLastName_validInput() {
        List<User> mockUsers = List.of(new User("user1", "hash", "email@example.com", ZoneId.of("UTC"), "John", "Doe"));
        when(userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "doe")).thenReturn(mockUsers);

        List<User> users = userBO.getUsersByFirstAndLastName(" John ", "doe ");

        assertEquals(1, users.size());
        verify(userRepository).findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "doe");
    }

    @Test
    void testGetUsersByFirstAndLastName_nullInput_throws() {
        assertThrows(IllegalArgumentException.class, () -> userBO.getUsersByFirstAndLastName(null, "Doe"));
        assertThrows(IllegalArgumentException.class, () -> userBO.getUsersByFirstAndLastName("John", null));
    }

    @Test
    void testCreateUser_success() {
        String username = "newUser";
        String email = "new@example.com";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        User savedUser = new User(username, "hash", email, ZoneId.of("UTC"), "First", "Last");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User created = userBO.createUser(username, "hash", email, ZoneId.of("UTC"), "First", "Last");

        assertEquals(username, created.getUsername());
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_usernameExists_throws() {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        DuplicateUsernameException ex = assertThrows(DuplicateUsernameException.class,
                () -> userBO.createUser("existingUser", "hash", "email@example.com", ZoneId.of("UTC"), "First", "Last"));
        assertEquals("User with username existingUser already exists", ex.getMessage());
    }

    @Test
    void testCreateUser_emailExists_throws() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        DuplicateEmailException ex = assertThrows(DuplicateEmailException.class,
                () -> userBO.createUser("user", "hash", "existing@example.com", ZoneId.of("UTC"), "First", "Last"));
        assertEquals("The email existing@example.com is already registered", ex.getMessage());
    }

    @Test
    void testUpdateUser_successfulUpdate() {
        Long userId = 1L;
        User existingUser = new User("oldUser", "oldHash", "old@example.com", ZoneId.of("UTC"), "OldFirst", "OldLast");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updates = new User("newUser", "newHash", "new@example.com", ZoneId.of("UTC"), "NewFirst", "NewLast");

        User updated = userBO.updateUser(userId, updates);

        assertEquals("newUser", updated.getUsername());
        assertEquals("newHash", updated.getPasswordHash());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("NewFirst", updated.getFirstName());
        assertEquals("NewLast", updated.getLastName());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateUser_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userBO.updateUser(99L, new User()));

        assertEquals("User with ID 99 not found", ex.getMessage());
    }

    @Test
    void testSetUserEnabled_success() {
        Long userId = 1L;
        User user = new User("user", "hash", "email@example.com", ZoneId.of("UTC"), "First", "Last");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User enabledUser = userBO.setUserEnabled(userId, true);

        assertTrue(enabledUser.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void testSetUserEnabled_userNotFound_throws() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userBO.setUserEnabled(42L, true));

        assertEquals("User with ID 42 not found", ex.getMessage());
    }
}
