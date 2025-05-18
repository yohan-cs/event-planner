package com.yohan.event_planner.service;

import static com.yohan.event_planner.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.yohan.event_planner.exception.ErrorCode.DUPLICATE_USERNAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.ZoneId;

import com.yohan.event_planner.business.UserBO;
import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.dto.UserCreateDTO;
import com.yohan.event_planner.dto.UserResponseDTO;
import com.yohan.event_planner.exception.EmailException;
import com.yohan.event_planner.exception.UsernameException;
import com.yohan.event_planner.mapper.UserMapper;
import com.yohan.event_planner.domain.User;

import com.yohan.event_planner.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class UserServiceImplTest {

    @Mock
    private UserBO userBO;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateDTO validCreateDTO;
    private User fakeUser;
    private UserResponseDTO expectedResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validCreateDTO = new UserCreateDTO(
                "testuser",
                "plaintextPassword",
                "test@example.com",
                "Test",
                "User",
                "America/New_York"
        );

        fakeUser = new User(
                "user1",
                new PasswordVO("plainPassword123", TestConstants.PASSWORD_ENCODER),
                "user1@example.com",
                ZoneId.of("UTC"),
                "First",
                "Last"
        );

        expectedResponseDTO = new UserResponseDTO(
                fakeUser.getId(),
                fakeUser.getUsername(),
                fakeUser.getEmail(),
                fakeUser.getFirstName(),
                fakeUser.getLastName(),
                fakeUser.getTimezone().toString()
        );
    }

    @Test
    void createUser_shouldReturnDto() {
        // Arrange
        when(userMapper.toEntity(any(UserCreateDTO.class), anyString())).thenReturn(fakeUser);
        when(userBO.createUser(
                eq(fakeUser.getUsername()),
                eq(fakeUser.getPasswordHash()),
                eq(fakeUser.getEmail()),
                eq(fakeUser.getTimezone()),
                eq(fakeUser.getFirstName()),
                eq(fakeUser.getLastName())
        )).thenReturn(fakeUser);
        when(userMapper.toDto(fakeUser)).thenReturn(expectedResponseDTO);

        // Act
        UserResponseDTO actualResponse = userService.createUser(validCreateDTO);

        // Assert
        assertEquals(expectedResponseDTO, actualResponse);

        verify(userBO).createUser(
                fakeUser.getUsername(),
                fakeUser.getPasswordHash(),
                fakeUser.getEmail(),
                fakeUser.getTimezone(),
                fakeUser.getFirstName(),
                fakeUser.getLastName()
        );
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        // Arrange
        when(userMapper.toEntity(any(UserCreateDTO.class), anyString())).thenReturn(fakeUser);
        when(userBO.createUser(anyString(), anyString(), anyString(), any(ZoneId.class), anyString(), anyString()))
                .thenThrow(new UsernameException(DUPLICATE_USERNAME,"testuser"));

        // Act & Assert
        UsernameException ex = assertThrows(UsernameException.class,
                () -> userService.createUser(validCreateDTO));
        assertTrue(ex.getMessage().contains("testuser"));
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        // Arrange
        when(userMapper.toEntity(any(UserCreateDTO.class), anyString())).thenReturn(fakeUser);
        when(userBO.createUser(anyString(), anyString(), anyString(), any(ZoneId.class), anyString(), anyString()))
                .thenThrow(new EmailException(DUPLICATE_EMAIL, "test@example.com"));

        // Act & Assert
        EmailException ex = assertThrows(EmailException.class,
                () -> userService.createUser(validCreateDTO));
        assertTrue(ex.getMessage().contains("test@example.com"));
    }
}
