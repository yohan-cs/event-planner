package com.yohan.event_planner.security;

import com.yohan.event_planner.util.TestUtils;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private final String base64Secret = "bW9ja1NlY3JldEtleU5vblByb2R1Y3Rpb25Dcml0aWNhbA==";
    private final long expirationMillis = 1000 * 60 * 60 * 24; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        TestUtils.setPrivateField(jwtService, "jwtSecret", base64Secret);
        TestUtils.setPrivateField(jwtService, "jwtExpirationMillis", expirationMillis);
    }

    @Test
    void generateToken_shouldContainUsernameAsSubject() {
        String token = jwtService.generateToken("testuser");
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidTokenAndCorrectUsername() {
        String token = jwtService.generateToken("alice");
        assertTrue(jwtService.isTokenValid(token, "alice"));
    }

    @Test
    void isTokenValid_shouldReturnFalseForIncorrectUsername() {
        String token = jwtService.generateToken("bob");
        assertFalse(jwtService.isTokenValid(token, "notbob"));
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() throws InterruptedException {
        TestUtils.setPrivateField(jwtService, "jwtExpirationMillis", 1L); // expire immediately
        String token = jwtService.generateToken("carol");
        Thread.sleep(5); // ensure expiration
        assertFalse(jwtService.isTokenValid(token, "carol"));
    }

    @Test
    void isTokenExpired_shouldReturnTrueForExpiredToken() throws InterruptedException {
        TestUtils.setPrivateField(jwtService, "jwtExpirationMillis", 1L);
        String token = jwtService.generateToken("dave");
        Thread.sleep(5);
        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_shouldReturnFalseForValidToken() {
        String token = jwtService.generateToken("eve");
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void extractUsername_shouldThrowForTamperedToken() {
        String token = jwtService.generateToken("frank");
        String tampered = token.replace('a', 'b'); // crude tampering

        assertThrows(SignatureException.class, () -> jwtService.extractUsername(tampered));
    }

    @Test
    void extractUsername_shouldThrowForMalformedToken() {
        String malformed = "not.a.valid.jwt";
        assertThrows(RuntimeException.class, () -> jwtService.extractUsername(malformed));
    }
}
