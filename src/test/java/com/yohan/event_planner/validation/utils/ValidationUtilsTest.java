package com.yohan.event_planner.validation.utils;

import com.yohan.event_planner.util.TestConstants;
import com.yohan.event_planner.validation.utils.ValidationUtils;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    // ---------- requireValidId ----------

    @Test
    void requireValidId_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireValidId(null, "testId"));
        assertEquals("testId must be a positive, non-null ID", ex.getMessage());
    }

    @Test
    void requireValidId_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireValidId(0L, "testId"));
        assertEquals("testId must be a positive, non-null ID", ex.getMessage());
    }

    @Test
    void requireValidId_shouldThrowException_whenIdIsNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireValidId(-10L, "testId"));
        assertEquals("testId must be a positive, non-null ID", ex.getMessage());
    }

    @Test
    void requireValidId_shouldPass_whenIdIsPositive() {
        assertDoesNotThrow(() -> ValidationUtils.requireValidId(1L, "testId"));
        assertDoesNotThrow(() -> ValidationUtils.requireValidId(123456L, "testId"));
    }

    // ---------- requireNonNull ----------

    @Test
    void requireNonNull_shouldThrowException_whenObjectIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonNull(null, "obj"));
        assertEquals("obj must not be null", ex.getMessage());
    }

    @Test
    void requireNonNull_shouldReturnObject_whenNotNull() {
        String str = "test";
        String result = ValidationUtils.requireNonNull(str, "str");
        assertSame(str, result);
    }

    // ---------- requireNonNullDateTime ----------

    @Test
    void requireNonNullDateTime_shouldThrowException_whenDateTimeIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonNullDateTime(null, "dateTime"));
        assertEquals("dateTime must not be null", ex.getMessage());
    }

    @Test
    void requireNonNullDateTime_shouldPass_whenDateTimeIsNotNull() {
        ZonedDateTime dt = TestConstants.MAY_20_2025_9AM;
        assertDoesNotThrow(() -> ValidationUtils.requireNonNullDateTime(dt, "dateTime"));
    }

    // ---------- requireNonEmptyCollection ----------

    @Test
    void requireNonEmptyCollection_shouldThrowException_whenCollectionIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmptyCollection(null, "coll"));
        assertEquals("coll must not be null or empty", ex.getMessage());
    }

    @Test
    void requireNonEmptyCollection_shouldThrowException_whenCollectionIsEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmptyCollection(Collections.emptyList(), "coll"));
        assertEquals("coll must not be null or empty", ex.getMessage());
    }

    @Test
    void requireNonEmptyCollection_shouldPass_whenCollectionIsNotEmpty() {
        List<String> list = List.of("item");
        assertDoesNotThrow(() -> ValidationUtils.requireNonEmptyCollection(list, "coll"));
    }
}
