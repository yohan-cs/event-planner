package com.yohan.event_planner.business.handler;

import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.UsernameException;
import com.yohan.event_planner.repository.UserRepository;
import com.yohan.event_planner.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPatchHandlerTest {

    private UserRepository userRepository;
    private UserPatchHandler patchHandler;

    private User existingUser;
    private final Long existingUserId = 1L;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        patchHandler = new UserPatchHandler(userRepository);

        existingUser = new User(
                "user1",
                new PasswordVO("plainPassword123", TestConstants.PASSWORD_ENCODER),
                "user1@example.com",
                ZoneId.of("UTC"),
                "First",
                "Last"
        );
        // Set the id field using reflection since it's private and no setter
        setId(existingUser, existingUserId);
    }

    // Helper to set private id field
    private void setId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void applyPatch_noChanges_returnsFalse() {
        User patch = new User(); // all null fields
        boolean result = patchHandler.applyPatch(existingUser, patch);
        assertFalse(result);
    }

    @Test
    void applyPatch_updatesUsernameSuccessfully() {
        String newUsername = "newUsername";

        when(userRepository.existsByUsernameAndIdNot(newUsername, existingUserId)).thenReturn(false);

        User patch = new User();
        patch.setUsername(newUsername);

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertTrue(result);
        assertEquals(newUsername, existingUser.getUsername());
        verify(userRepository).existsByUsernameAndIdNot(newUsername, existingUserId);
    }

    @Test
    void applyPatch_updatesUsername_throwsDuplicateUsernameException() {
        String newUsername = "takenUsername";

        when(userRepository.existsByUsernameAndIdNot(newUsername, existingUserId)).thenReturn(true);

        User patch = new User();
        patch.setUsername(newUsername);

        UsernameException ex = assertThrows(UsernameException.class,
                () -> patchHandler.applyPatch(existingUser, patch));

        assertEquals("User with username '" + newUsername + "' already exists", ex.getMessage());

        verify(userRepository).existsByUsernameAndIdNot(newUsername, existingUserId);
    }


    @Test
    void applyPatch_updatesPasswordHashSuccessfully() {
        String newPasswordHash = "newHash";

        User patch = new User();
        patch.setPasswordHash(newPasswordHash);

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertTrue(result);
        assertEquals(newPasswordHash, existingUser.getPasswordHash());
        verifyNoInteractions(userRepository);
    }

    @Test
    void applyPatch_updatesEmailSuccessfully() {
        String newEmail = "new@example.com";

        when(userRepository.existsByEmailAndIdNot(newEmail, existingUserId)).thenReturn(false);

        User patch = new User();
        patch.setEmail(newEmail);

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertTrue(result);
        assertEquals(newEmail, existingUser.getEmail());
        verify(userRepository).existsByEmailAndIdNot(newEmail, existingUserId);
    }

    @Test
    void applyPatch_updatesEmail_throwsDuplicateEmailException() {
        String newEmail = "taken@example.com";

        when(userRepository.existsByEmailAndIdNot(newEmail, existingUserId)).thenReturn(true);

        User patch = new User();
        patch.setEmail(newEmail);

        EmailException ex = assertThrows(EmailException.class,
                () -> patchHandler.applyPatch(existingUser, patch));

        assertEquals("The email '" + newEmail + "' is already registered.", ex.getMessage());

        verify(userRepository).existsByEmailAndIdNot(newEmail, existingUserId);
    }

    @Test
    void applyPatch_updatesFirstNameSuccessfully() {
        String newFirstName = "NewFirst";

        User patch = new User();
        patch.setFirstName(newFirstName);

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertTrue(result);
        assertEquals(newFirstName, existingUser.getFirstName());
        verifyNoInteractions(userRepository);
    }

    @Test
    void applyPatch_updatesLastNameSuccessfully() {
        String newLastName = "NewLast";

        User patch = new User();
        patch.setLastName(newLastName);

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertTrue(result);
        assertEquals(newLastName, existingUser.getLastName());
        verifyNoInteractions(userRepository);
    }

    @Test
    void applyPatch_updatesTimezoneSuccessfully() {
        ZoneId newZone = ZoneId.of("America/New_York");

        User patch = new User();
        patch.setTimezone(newZone);

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertTrue(result);
        assertEquals(newZone, existingUser.getTimezone());
        verifyNoInteractions(userRepository);
    }

    @Test
    void applyPatch_multipleFieldsUpdated_successful() {
        String newUsername = "newUser";
        String newEmail = "newEmail@example.com";
        String newFirstName = "NewFirst";
        ZoneId newZone = ZoneId.of("Asia/Tokyo");

        when(userRepository.existsByUsernameAndIdNot(newUsername, existingUserId)).thenReturn(false);
        when(userRepository.existsByEmailAndIdNot(newEmail, existingUserId)).thenReturn(false);

        User patch = new User();
        patch.setUsername(newUsername);
        patch.setEmail(newEmail);
        patch.setFirstName(newFirstName);
        patch.setTimezone(newZone);

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertTrue(result);
        assertEquals(newUsername, existingUser.getUsername());
        assertEquals(newEmail, existingUser.getEmail());
        assertEquals(newFirstName, existingUser.getFirstName());
        assertEquals(newZone, existingUser.getTimezone());

        verify(userRepository).existsByUsernameAndIdNot(newUsername, existingUserId);
        verify(userRepository).existsByEmailAndIdNot(newEmail, existingUserId);
    }

    @Test
    void applyPatch_unchangedFields_doNotUpdate() {
        User patch = new User();
        patch.setUsername(existingUser.getUsername());
        patch.setPasswordHash(existingUser.getPasswordHash());
        patch.setEmail(existingUser.getEmail());
        patch.setFirstName(existingUser.getFirstName());
        patch.setLastName(existingUser.getLastName());
        patch.setTimezone(existingUser.getTimezone());

        boolean result = patchHandler.applyPatch(existingUser, patch);

        assertFalse(result);

        // No repository calls should be made since no changes
        verifyNoInteractions(userRepository);
    }
}
