package com.yohan.event_planner.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ZoneIdValidatorTest {

    private ZoneIdValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ZoneIdValidator();
        context = mock(ConstraintValidatorContext.class);  // context is unused in validation logic
        validator.initialize(null);  // no-op here
    }

    @Test
    void isValid_shouldReturnTrue_forNullInput() {
        assertTrue(validator.isValid(null, context), "Null should be valid");
    }

    @Test
    void isValid_shouldReturnTrue_forBlankInput() {
        assertTrue(validator.isValid("", context), "Empty string should be valid");
        assertTrue(validator.isValid("   ", context), "Whitespace string should be valid");
    }

    @Test
    void isValid_shouldReturnTrue_forValidZoneIdStrings() {
        assertTrue(validator.isValid("UTC", context));
        assertTrue(validator.isValid("America/New_York", context));
        assertTrue(validator.isValid("Europe/London", context));
    }

    @Test
    void isValid_shouldReturnFalse_forInvalidZoneIdStrings() {
        assertFalse(validator.isValid("Invalid/Zone", context));
        assertFalse(validator.isValid("Mars/Phobos", context));
        assertFalse(validator.isValid("Not a timezone", context));
    }
}
