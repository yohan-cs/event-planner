package com.yohan.event_planner.validation;

import com.yohan.event_planner.exception.ConflictException;
import com.yohan.event_planner.exception.InvalidTimeException;
import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.Event;
import com.yohan.event_planner.model.User;
import com.yohan.event_planner.util.TestConstants;
import com.yohan.event_planner.util.TestUtils;
import com.yohan.event_planner.validation.EventValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventValidatorTest {

    private EventValidator validator;
    private User testUser;

    @BeforeEach
    void setUp() {
        validator = new EventValidator();
        testUser = TestConstants.TEST_USER;
    }

    // --- validateStartBeforeEnd tests ---

    @Test
    void validateStartBeforeEnd_shouldNotThrow_whenStartIsBeforeEnd() {
        ZonedDateTime start = TestConstants.MAY_20_2025_9AM;
        ZonedDateTime end = TestConstants.MAY_20_2025_11AM;

        assertDoesNotThrow(() -> validator.validateStartBeforeEnd(start, end));
    }

    @Test
    void validateStartBeforeEnd_shouldThrowInvalidTimeException_whenStartEqualsEnd() {
        ZonedDateTime start = TestConstants.MAY_20_2025_9AM;
        ZonedDateTime end = TestConstants.MAY_20_2025_9AM;

        InvalidTimeException ex = assertThrows(InvalidTimeException.class,
                () -> validator.validateStartBeforeEnd(start, end));
        assertTrue(ex.getMessage().contains("Start time"));
    }

    @Test
    void validateStartBeforeEnd_shouldThrowInvalidTimeException_whenStartAfterEnd() {
        ZonedDateTime start = TestConstants.MAY_20_2025_11AM;
        ZonedDateTime end = TestConstants.MAY_20_2025_9AM;

        InvalidTimeException ex = assertThrows(InvalidTimeException.class,
                () -> validator.validateStartBeforeEnd(start, end));
        assertTrue(ex.getMessage().contains("Start time"));
    }

    // --- validateNoConflicts tests ---

    @Test
    void validateNoConflicts_shouldNotThrow_whenDayHasNoEvents() {
        ZonedDateTime start = TestConstants.MAY_20_2025_9AM;
        ZonedDateTime end = TestConstants.MAY_20_2025_11AM;
        Day day = new Day();
        day.setDate(TestConstants.MAY_20_2025_9AM.toLocalDate());
        day.setArchived(false);
        // No events added => empty set
        assertDoesNotThrow(() -> validator.validateNoConflicts(start, end, null, day));
    }

    @Test
    void validateNoConflicts_shouldNotThrow_whenNoEventsOverlap() {
        ZonedDateTime start = TestConstants.MAY_20_2025_1PM;
        ZonedDateTime end = TestConstants.MAY_20_2025_2PM;

        Day day = new Day();
        day.setDate(TestConstants.MAY_20_2025_9AM.toLocalDate());

        Event existingEvent = TestUtils.createEventWithId(
                TestConstants.EVENT_ID_1,
                TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM,
                TestConstants.MAY_20_2025_11AM,
                testUser
        );

        day.getEvents().add(existingEvent);

        assertDoesNotThrow(() -> validator.validateNoConflicts(start, end, null, day));
    }

    @Test
    void validateNoConflicts_shouldThrowConflictException_whenNewEventOverlapsExisting() {
        ZonedDateTime start = TestConstants.MAY_20_2025_10AM;
        ZonedDateTime end = TestConstants.MAY_20_2025_NOON;

        Day day = new Day();
        day.setDate(TestConstants.MAY_20_2025_9AM.toLocalDate());

        Event existingEvent = TestUtils.createEventWithId(
                TestConstants.EVENT_ID_1,
                TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM,
                TestConstants.MAY_20_2025_11AM,
                testUser
        );

        day.getEvents().add(existingEvent);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> validator.validateNoConflicts(start, end, null, day));
        assertTrue(ex.getMessage().contains(existingEvent.getName()));
    }

    @Test
    void validateNoConflicts_shouldNotThrow_whenOverlapWithExcludedEventId() {
        ZonedDateTime start = TestConstants.MAY_20_2025_10AM;
        ZonedDateTime end = TestConstants.MAY_20_2025_NOON;

        Day day = new Day();
        day.setDate(TestConstants.MAY_20_2025_9AM.toLocalDate());

        Event existingEvent = TestUtils.createEventWithId(
                TestConstants.EVENT_ID_1,
                TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM,
                TestConstants.MAY_20_2025_11AM,
                testUser
        );

        day.getEvents().add(existingEvent);

        // excludeEventId equals existingEvent id should skip conflict
        assertDoesNotThrow(() -> validator.validateNoConflicts(start, end, TestConstants.EVENT_ID_1, day));
    }

    @Test
    void validateNoConflicts_shouldNotThrow_whenEventsTouchButDoNotOverlap() {
        // existing event: 9-11AM, new event: 11AM-1PM (should NOT conflict)
        ZonedDateTime start = TestConstants.MAY_20_2025_11AM;
        ZonedDateTime end = TestConstants.MAY_20_2025_1PM;

        Day day = new Day();
        day.setDate(TestConstants.MAY_20_2025_9AM.toLocalDate());

        Event existingEvent = TestUtils.createEventWithId(
                TestConstants.EVENT_ID_1,
                TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM,
                TestConstants.MAY_20_2025_11AM,
                testUser
        );

        day.getEvents().add(existingEvent);

        assertDoesNotThrow(() -> validator.validateNoConflicts(start, end, null, day));
    }
}
