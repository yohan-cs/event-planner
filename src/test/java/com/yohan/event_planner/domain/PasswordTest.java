//package com.yohan.event_planner.domain;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//class PasswordTest {
//
//    @Test
//    @DisplayName("Constructing Password with valid input succeeds")
//    void constructor_validPassword_doesNotThrow() {
//        assertDoesNotThrow(() -> new Password("ValidPass123"));
//    }
//
//    @Test
//    @DisplayName("Constructing Password with null throws IllegalArgumentException")
//    void constructor_nullPassword_throwsException() {
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Password(null));
//        assertEquals("Password cannot be null or blank", ex.getMessage());
//    }
//
//    @Test
//    @DisplayName("Constructing Password with blank string throws IllegalArgumentException")
//    void constructor_blankPassword_throwsException() {
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Password(" "));
//        assertEquals("Password cannot be null or blank", ex.getMessage());
//    }
//
//    @Test
//    @DisplayName("Constructing Password with less than 8 characters throws IllegalArgumentException")
//    void constructor_shortPassword_throwsException() {
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Password("1234567"));
//        assertEquals("Password must be at least 8 characters long", ex.getMessage());
//    }
//
//    @Test
//    @DisplayName("Passwords with same value are equal")
//    void equals_sameValue_returnsTrue() {
//        Password p1 = new Password("SamePassword123");
//        Password p2 = new Password("SamePassword123");
//
//        assertEquals(p1, p2);
//        assertEquals(p1.hashCode(), p2.hashCode());
//    }
//
//    @Test
//    @DisplayName("Passwords with different values are not equal")
//    void equals_differentValue_returnsFalse() {
//        Password p1 = new Password("PasswordOne");
//        Password p2 = new Password("PasswordTwo");
//
//        assertNotEquals(p1, p2);
//    }
//
//    @Test
//    @DisplayName("toString does not expose actual password value")
//    void toString_doesNotRevealPassword() {
//        Password p = new Password("SecretPass123");
//        String toString = p.toString();
//
//        assertFalse(toString.contains("SecretPass123"));
//        assertTrue(toString.contains("****"));
//    }
//}
