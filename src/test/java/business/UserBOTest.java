package business;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import dao.UserDAO;
import model.User;
import exception.InvalidTimezoneException;
import exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import repository.UserRepository;

import java.time.ZoneId;
import java.util.Optional;

public class UserBOTest {

    private UserRepository userRepository;
    private UserBO userBO;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        userBO = new UserBO(userRepository);
    }

    @Test
    public void testCreateUser_valid() {
        // Setup the test data
        String username = "testUser";
        String passwordHash = "hashedPassword";
        String email = "test@example.com";
        ZoneId timezone = ZoneId.of("UTC");

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setEmail(email);
        user.setTimezone(timezone);

        // Mock repository methods
        when(userRepository.existsByUsername(username)).thenReturn(false);  // Mock check for existing username
        when(userRepository.existsByEmail(email)).thenReturn(false);  // Mock check for existing email
        when(userRepository.save(any(User.class))).thenReturn(user);  // Mock saving the user

        // Call createUser method
        User result = userBO.createUser(username, passwordHash, email, timezone);

        // Verify the results
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).existsByUsername(username);  // Verify username existence check
        verify(userRepository).existsByEmail(email);  // Verify email existence check
        verify(userRepository).save(userCaptor.capture());  // Capture the argument passed to save()

        User savedUser = userCaptor.getValue();  // Get the captured user
        assertNotNull(savedUser);
        assertEquals(username, savedUser.getUsername());  // Assert username is correct
        assertEquals(email, savedUser.getEmail());  // Assert email is correct
        assertEquals(timezone, savedUser.getTimezone());  // Assert timezone is correct
        assertEquals(passwordHash, savedUser.getPasswordHash());  // Assert password hash is correct

        // Compare field values, not object references
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(timezone, result.getTimezone());
        assertEquals(passwordHash, result.getPasswordHash());
    }

    @Test
    public void testCreateUser_usernameAlreadyExists() {
        // Setup the test data
        String username = "testUser";
        String passwordHash = "hashedPassword";
        String email = "test@example.com";
        ZoneId timezone = ZoneId.of("UTC");

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setEmail(email);
        user.setTimezone(timezone);

        // Mock repository methods
        when(userRepository.existsByUsername(username)).thenReturn(true);  // Username already exists
        when(userRepository.existsByEmail(email)).thenReturn(false);  // Email doesn't exist

        // Call createUser method and expect an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userBO.createUser(username, passwordHash, email, timezone);
        });

        // Verify exception message
        assertEquals("Username already exists", exception.getMessage());

        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_usernameAlreadyExists() {
        // Setup the test data
        String existingUsername = "existingUser";
        String newUsername = "newUser";
        String passwordHash = "newPasswordHash";
        String email = "test@example.com";
        ZoneId timezone = ZoneId.of("UTC");

        // Creating the existing user mock with an auto-generated ID
        User existingUser = new User(existingUsername, passwordHash, email, timezone);

        // Creating the updated user with a new username
        User updatedUser = new User(newUsername, passwordHash, email, timezone);

        // Mock repository methods
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(newUsername)).thenReturn(true);  // Username already exists

        // Call updateUser method and expect an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userBO.updateUser(1L, updatedUser);
        });

        // Verify exception message
        assertEquals("Username already exists", exception.getMessage());

        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_emailAlreadyExists() {
        // Setup the test data
        String existingUsername = "existingUser";
        String newUsername = "newUser";
        String passwordHash = "newPasswordHash";
        String existingEmail = "existing@example.com";
        String newEmail = "new@example.com";
        ZoneId timezone = ZoneId.of("UTC");

        // Creating the existing user mock with an auto-generated ID and existing email
        User existingUser = new User(existingUsername, passwordHash, existingEmail, timezone);

        // Creating the updated user with a new email
        User updatedUser = new User(newUsername, passwordHash, newEmail, timezone);

        // Mock repository methods
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(true);  // Email already exists

        // Call updateUser method and expect an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userBO.updateUser(1L, updatedUser);
        });

        // Verify exception message
        assertEquals("Email already exists", exception.getMessage());

        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_userNotFound() {
        // Setup the test data
        String newUsername = "newUser";
        String passwordHash = "newPasswordHash";
        String newEmail = "new@example.com";
        ZoneId timezone = ZoneId.of("UTC");

        // Creating the updated user
        User updatedUser = new User(newUsername, passwordHash, newEmail, timezone);

        // Mock repository method to return empty Optional (user not found)
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Call updateUser method and expect an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userBO.updateUser(1L, updatedUser);
        });

        // Verify exception message
        assertEquals("User not found", exception.getMessage());

        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_timezoneUpdate() {
        // Setup the test data
        String existingUsername = "existingUser";
        String newUsername = "updatedUser";
        String passwordHash = "newPasswordHash";
        String newEmail = "new@example.com";
        ZoneId existingTimezone = ZoneId.of("UTC");
        ZoneId newTimezone = ZoneId.of("Europe/London");

        // Create the existing user mock
        User existingUser = new User(existingUsername, passwordHash, "existing@example.com", existingTimezone);

        // Mock repository methods
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);  // Mock the save operation

        // Create the updated user with new data
        User updatedUser = new User(newUsername, passwordHash, newEmail, newTimezone);

        // Call the actual updateUser method
        User result = userBO.updateUser(1L, updatedUser);

        // Verify that the timezone, username, and email were updated
        assertEquals(newTimezone, result.getTimezone());
        assertEquals(newUsername, result.getUsername());
        assertEquals(newEmail, result.getEmail());

        // Verify that save was called
        verify(userRepository).save(any(User.class));
    }
}
