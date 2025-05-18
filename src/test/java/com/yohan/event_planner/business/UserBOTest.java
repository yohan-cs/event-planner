package com.yohan.event_planner.business;

import com.yohan.event_planner.business.handler.UserPatchHandler;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.UserNotFoundException;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.exception.UsernameException;
import com.yohan.event_planner.repository.UserRepository;
import com.yohan.event_planner.util.TestConstants;
import com.yohan.event_planner.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserBOTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPatchHandler userPatchHandler;

    @InjectMocks
    private UserBO userBO;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleUser = new User("user1", "hash", "user1@example.com", ZoneId.of("UTC"), "First", "Last");
        TestUtils.setId(sampleUser, TestConstants.USER_ID_1);
    }

    // getById(Long userId)

    @Test
    void getById_WithValidId_ReturnsUserOptional() {
        when(userRepository.findById(TestConstants.USER_ID_1)).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userBO.getUserById(TestConstants.USER_ID_1);

        assertTrue(result.isPresent());
        assertEquals(sampleUser, result.get());
        verify(userRepository).findById(TestConstants.USER_ID_1);
    }

    @Test
    void getById_WithNullId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.getUserById(null));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getById_WithInvalidId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.getUserById(-5L));
        verify(userRepository, never()).findById(any());
    }

    // getUsersByFirstAndLastName(String firstName, String lastName)

    @Test
    void getUsersByFirstAndLastName_WithValidNames_ReturnsUserList() {
        List<User> expectedList = List.of(sampleUser);
        when(userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("First", "Last")).thenReturn(expectedList);

        List<User> result = userBO.getUsersByFirstAndLastName("First", "Last");

        assertEquals(expectedList, result);
        verify(userRepository).findByFirstNameIgnoreCaseAndLastNameIgnoreCase("First", "Last");
    }

    @Test
    void getUsersByFirstAndLastName_WithTrimmedNames_CallsRepositoryWithTrimmed() {
        List<User> expectedList = List.of(sampleUser);
        when(userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("First", "Last")).thenReturn(expectedList);

        List<User> result = userBO.getUsersByFirstAndLastName(" First ", " Last ");

        assertEquals(expectedList, result);
        verify(userRepository).findByFirstNameIgnoreCaseAndLastNameIgnoreCase("First", "Last");
    }

    @Test
    void getUsersByFirstAndLastName_WithNullFirstName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.getUsersByFirstAndLastName(null, "Last"));
        verify(userRepository, never()).findByFirstNameIgnoreCaseAndLastNameIgnoreCase(anyString(), anyString());
    }

    @Test
    void getUsersByFirstAndLastName_WithNullLastName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.getUsersByFirstAndLastName("First", null));
        verify(userRepository, never()).findByFirstNameIgnoreCaseAndLastNameIgnoreCase(anyString(), anyString());
    }

    // createUser(String username, String passwordHash, String email, ZoneId timezone, String firstName, String lastName)

    @Test
    void createUser_WithUniqueUsernameAndEmail_SavesAndReturnsUser() {
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        User createdUser = new User("newUser", "hash", "new@example.com", ZoneId.of("UTC"), "First", "Last");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            TestUtils.setId(arg, TestConstants.USER_ID_2);
            return arg;
        });

        User result = userBO.createUser("newUser", "hash", "new@example.com", ZoneId.of("UTC"), "First", "Last");

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(TestConstants.USER_ID_2, result.getId());
        verify(userRepository).existsByUsername("newUser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateUsername_ThrowsDuplicateUsernameException() {
        when(userRepository.existsByUsername("takenUser")).thenReturn(true);

        assertThrows(UsernameException.class, () -> userBO.createUser("takenUser", "hash", "email@example.com", ZoneId.of("UTC"), "First", "Last"));

        verify(userRepository).existsByUsername("takenUser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WithDuplicateEmail_ThrowsDuplicateEmailException() {
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(EmailException.class, () -> userBO.createUser("newUser", "hash", "taken@example.com", ZoneId.of("UTC"), "First", "Last"));

        verify(userRepository).existsByUsername("newUser");
        verify(userRepository).existsByEmail("taken@example.com");
        verify(userRepository, never()).save(any());
    }

    // updateUser(Long userId, User updatedUser)

    @Test
    void updateUser_WithValidUserIdAndUpdates_AppliesPatchAndSaves() {
        User updatedUser = new User();
        when(userRepository.findById(TestConstants.USER_ID_1)).thenReturn(Optional.of(sampleUser));
        when(userPatchHandler.applyPatch(sampleUser, updatedUser)).thenReturn(true);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User result = userBO.updateUser(TestConstants.USER_ID_1, updatedUser);

        assertEquals(sampleUser, result);
        assertNotNull(result.getUpdatedDate());
        verify(userRepository).findById(TestConstants.USER_ID_1);
        verify(userPatchHandler).applyPatch(sampleUser, updatedUser);
        verify(userRepository).save(sampleUser);
    }

    @Test
    void updateUser_WithValidUserIdButNoChanges_DoesNotSave() {
        User updatedUser = new User();
        when(userRepository.findById(TestConstants.USER_ID_1)).thenReturn(Optional.of(sampleUser));
        when(userPatchHandler.applyPatch(sampleUser, updatedUser)).thenReturn(false);

        User result = userBO.updateUser(TestConstants.USER_ID_1, updatedUser);

        assertEquals(sampleUser, result);
        assertNull(result.getUpdatedDate());
        verify(userRepository).findById(TestConstants.USER_ID_1);
        verify(userPatchHandler).applyPatch(sampleUser, updatedUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WithNonExistentUserId_ThrowsUserNotFoundException() {
        when(userRepository.findById(TestConstants.USER_ID_1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userBO.updateUser(TestConstants.USER_ID_1, new User()));

        verify(userRepository).findById(TestConstants.USER_ID_1);
        verify(userPatchHandler, never()).applyPatch(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WithNullUserId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.updateUser(null, new User()));
        verify(userRepository, never()).findById(any());
    }

    // setUserEnabled(Long userId, boolean enabled)

    @Test
    void setUserEnabled_WithValidUserId_EnablesUserAndSaves() {
        when(userRepository.findById(TestConstants.USER_ID_1)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        User result = userBO.setUserEnabled(TestConstants.USER_ID_1, true);

        assertTrue(result.isEnabled());
        assertNotNull(result.getUpdatedDate());
        verify(userRepository).findById(TestConstants.USER_ID_1);
        verify(userRepository).save(sampleUser);
    }

    @Test
    void setUserEnabled_WithNonExistentUserId_ThrowsUserNotFoundException() {
        when(userRepository.findById(TestConstants.USER_ID_1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userBO.setUserEnabled(TestConstants.USER_ID_1, false));

        verify(userRepository).findById(TestConstants.USER_ID_1);
        verify(userRepository, never()).save(any());
    }

    @Test
    void setUserEnabled_WithNullUserId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.setUserEnabled(null, true));
        verify(userRepository, never()).findById(any());
    }

    // deleteById(Long userId)

    @Test
    void deleteById_WithValidUserId_DeletesUser() {
        assertDoesNotThrow(() -> userBO.deleteById(TestConstants.USER_ID_1));
        verify(userRepository).deleteById(TestConstants.USER_ID_1);
    }

    @Test
    void deleteById_WithNullUserId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.deleteById(null));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_WithInvalidUserId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userBO.deleteById(-1L));
        verify(userRepository, never()).deleteById(any());
    }
}
