package com.yohan.event_planner.business;

import com.yohan.event_planner.business.handler.UserPatchHandler;
import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.UserNotFoundException;
import com.yohan.event_planner.exception.UsernameException;
import com.yohan.event_planner.exception.PasswordException;
import com.yohan.event_planner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserBOTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPatchHandler userPatchHandler;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserBO userBO;

    private final String validUsername = "validUser";
    private final String validEmail = "valid@example.com";
    private final String rawPassword = "StrongPass123!";
    private final ZoneId zoneId = ZoneId.of("UTC");
    private final String firstName = "John";
    private final String lastName = "Doe";

    @BeforeEach
    void setup() {
        // Optional default stubbing if needed
    }

    // --- getUserById ---

    @Test
    void getUserById_validId_returnsUser() throws PasswordException {
        PasswordVO pw = new PasswordVO("hashedPassword");
        User user = new User(validUsername, pw, validEmail, zoneId, firstName, lastName);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userBO.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals(validUsername, result.get().getUsername());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_nullId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.getUserById(null));
        verifyNoInteractions(userRepository);
    }

    @Test
    void getUserById_invalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.getUserById(-5L));
        verifyNoInteractions(userRepository);
    }

    // --- getUsersByFirstAndLastName ---

    @Test
    void getUsersByFirstAndLastName_validInputs_returnsList() throws PasswordException {
        PasswordVO pw = new PasswordVO("hashedPassword");
        User user1 = new User("user1", pw, "u1@example.com", zoneId, "John", "Doe");
        User user2 = new User("user2", pw, "u2@example.com", zoneId, "John", "Doe");

        List<User> users = List.of(user1, user2);

        when(userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe")).thenReturn(users);

        List<User> result = userBO.getUsersByFirstAndLastName("  John  ", "Doe");

        assertEquals(2, result.size());
        verify(userRepository).findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe");
    }

    @Test
    void getUsersByFirstAndLastName_nullFirstName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userBO.getUsersByFirstAndLastName(null, "Doe"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void getUsersByFirstAndLastName_nullLastName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userBO.getUsersByFirstAndLastName("John", null));
        verifyNoInteractions(userRepository);
    }

    // --- createUser ---

    @Test
    void createUser_uniqueUsernameAndEmail_createsAndReturnsUser() throws PasswordException {
        when(userRepository.existsByUsername(validUsername)).thenReturn(false);
        when(userRepository.existsByEmail(validEmail)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn("hashedPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userBO.createUser(validUsername, rawPassword, validEmail, zoneId, firstName, lastName);

        assertNotNull(result);
        assertEquals(validUsername, result.getUsername());
        assertEquals(validEmail, result.getEmail());
        assertEquals(zoneId, result.getTimezone());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertNotNull(result.getPasswordHash());

        verify(userRepository).existsByUsername(validUsername);
        verify(userRepository).existsByEmail(validEmail);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_duplicateUsername_throwsUsernameException() {
        when(userRepository.existsByUsername(validUsername)).thenReturn(true);

        assertThrows(UsernameException.class, () ->
                userBO.createUser(validUsername, rawPassword, validEmail, zoneId, firstName, lastName));

        verify(userRepository).existsByUsername(validUsername);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_duplicateEmail_throwsEmailException() {
        when(userRepository.existsByUsername(validUsername)).thenReturn(false);
        when(userRepository.existsByEmail(validEmail)).thenReturn(true);

        assertThrows(EmailException.class, () ->
                userBO.createUser(validUsername, rawPassword, validEmail, zoneId, firstName, lastName));

        verify(userRepository).existsByUsername(validUsername);
        verify(userRepository).existsByEmail(validEmail);
        verify(userRepository, never()).save(any());
    }

    // --- updateUser ---

    @Test
    void updateUser_validId_existingUser_patchApplied_returnsUpdatedUser() throws PasswordException {
        PasswordVO pw = new PasswordVO("hashedPassword");
        User existingUser = new User(validUsername, pw, validEmail, zoneId, firstName, lastName);
        User updatedUser = new User(validUsername, pw, validEmail, zoneId, "NewFirst", lastName);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userPatchHandler.applyPatch(existingUser, updatedUser)).thenAnswer(invocation -> {
            User existing = invocation.getArgument(0);
            User updated = invocation.getArgument(1);
            existing.setFirstName(updated.getFirstName());  // apply patch mutation
            return true;
        });
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userBO.updateUser(1L, updatedUser);

        assertEquals(existingUser, result);
        assertEquals("NewFirst", result.getFirstName());
        assertNotNull(result.getUpdatedDate());
        verify(userRepository).findById(1L);
        verify(userPatchHandler).applyPatch(existingUser, updatedUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_validId_existingUser_noPatchApplied_returnsExistingUser() throws PasswordException {
        PasswordVO pw = new PasswordVO("hashedPassword");
        User existingUser = new User(validUsername, pw, validEmail, zoneId, firstName, lastName);
        User updatedUser = new User(validUsername, pw, validEmail, zoneId, firstName, lastName);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userPatchHandler.applyPatch(existingUser, updatedUser)).thenReturn(false);

        User result = userBO.updateUser(1L, updatedUser);

        assertEquals(existingUser, result);
        verify(userRepository).findById(1L);
        verify(userPatchHandler).applyPatch(existingUser, updatedUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_nonExistingUser_throwsUserNotFoundException() throws PasswordException {
        User updatedUser = new User(validUsername, new PasswordVO("hashedPassword"), validEmail, zoneId, firstName, lastName);

        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userBO.updateUser(42L, updatedUser));

        verify(userRepository).findById(42L);
        verify(userPatchHandler, never()).applyPatch(any(), any());
        verify(userRepository, never()).save(any());
    }

    // --- setUserEnabled ---

    @Test
    void setUserEnabled_validId_existingUser_setsEnabled() throws PasswordException {
        PasswordVO pw = new PasswordVO("hashedPassword");
        User user = new User(validUsername, pw, validEmail, zoneId, firstName, lastName);

        // If you don't have a setEnabled setter, you'll need to create the user with enabled flag constructor or use reflection
        // Here I assume setEnabled(boolean) exists. If not, let me know.
        user.setEnabled(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userBO.setUserEnabled(1L, true);

        assertTrue(result.isEnabled());
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void setUserEnabled_nonExistingUser_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userBO.setUserEnabled(99L, true));
        verify(userRepository).findById(99L);
        verify(userRepository, never()).save(any());
    }
}
