package com.yohan.event_planner.domain;

import com.yohan.event_planner.exception.PasswordException;
import com.yohan.event_planner.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordVOTest {

    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    void shouldCreatePasswordVOWithValidPassword() {
        assertDoesNotThrow(() -> new PasswordVO("StrongPass123!", encoder));
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        PasswordException ex = assertThrows(
                PasswordException.class,
                () -> new PasswordVO(null, encoder)
        );
        assertEquals(ErrorCode.NULL_PASSWORD, ex.getErrorCode());
    }

    @Test
    void shouldThrowExceptionForShortPassword() {
        PasswordException ex = assertThrows(
                PasswordException.class,
                () -> new PasswordVO("123", encoder)
        );
        assertEquals(ErrorCode.INVALID_PASSWORD_LENGTH, ex.getErrorCode());
    }

    @Test
    void shouldHashPasswordAndStoreOnlyHash() {
        String raw = "MySecurePassword1!";
        PasswordVO vo = new PasswordVO(raw, encoder);

        assertNotEquals(raw, vo.getHashedPassword());
        assertTrue(vo.getHashedPassword().startsWith("$2")); // BCrypt hash prefix
    }

    @Test
    void shouldMatchCorrectRawPassword() {
        String raw = "AnotherSecurePass!";
        PasswordVO vo = new PasswordVO(raw, encoder);

        assertTrue(vo.matches(raw, encoder));
    }

    @Test
    void shouldNotMatchIncorrectPassword() {
        String raw = "CorrectHorseBatteryStaple";
        PasswordVO vo = new PasswordVO(raw, encoder);

        assertFalse(vo.matches("WrongPassword", encoder));
    }

    @Test
    void shouldNotBeEqualDueToSaltedHashes() {
        String raw = "ConsistentPass123!";
        PasswordVO vo1 = new PasswordVO(raw, encoder);
        PasswordVO vo2 = new PasswordVO(raw, encoder);

        assertNotEquals(vo1.getHashedPassword(), vo2.getHashedPassword());
        assertNotEquals(vo1, vo2);
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        PasswordException ex = assertThrows(
                PasswordException.class,
                () -> new PasswordVO("", encoder)
        );
        assertEquals(ErrorCode.INVALID_PASSWORD_LENGTH, ex.getErrorCode());
    }

    @Test
    void matchesShouldReturnFalseForNullRawPassword() {
        PasswordVO vo = new PasswordVO("ValidPassword123", encoder);
        assertFalse(vo.matches(null, encoder));
    }
}
