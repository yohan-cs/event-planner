package com.yohan.event_planner.business.handler;

import static com.yohan.event_planner.util.TestConstants.*;
import static com.yohan.event_planner.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.exception.ConflictException;
import com.yohan.event_planner.exception.InvalidTimeException;
import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.Event;
import com.yohan.event_planner.model.User;
import com.yohan.event_planner.validation.EventValidator;
import com.yohan.event_planner.service.DayService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class EventPatchHandlerTest {

    private EventValidator eventValidator;
    private DayService dayService;
    private User creator;

    private Event existingEvent;

    @BeforeEach
    void setUp() {
        eventValidator = mock(EventValidator.class);
        dayService = mock(DayService.class);
        creator = TEST_USER;

        existingEvent = createEventWithId(EVENT_ID_1, EVENT_WORKOUT, MAY_20_2025_9AM, MAY_20_2025_11AM, creator);
        existingEvent.setDescription("Original description");
        existingEvent.setTimezone(ZoneId.of("UTC"));
    }

    @Test
    void applyPatch_updatesNameOnly() {
        EventUpdateDTO dto = createEventUpdateDTO(EVENT_UPDATED, null, null, null);

        EventPatchHandler.PatchResult result = EventPatchHandler.applyPatch(existingEvent, dto, eventValidator, dayService, creator);

        assertTrue(result.isUpdated());
        assertEquals(EVENT_UPDATED, existingEvent.getName());
        assertNull(result.getNewDays());

        verifyNoInteractions(eventValidator);
        verifyNoInteractions(dayService);
    }

    @Test
    void applyPatch_updatesDescriptionOnly() {
        EventUpdateDTO dto = createEventUpdateDTO(null, "New Description", null, null);

        EventPatchHandler.PatchResult result = EventPatchHandler.applyPatch(existingEvent, dto, eventValidator, dayService, creator);

        assertTrue(result.isUpdated());
        assertEquals("New Description", existingEvent.getDescription());
        assertNull(result.getNewDays());

        verifyNoInteractions(eventValidator);
        verifyNoInteractions(dayService);
    }

    @Test
    void applyPatch_updatesStartAndEndTimeAndTimezone() {
        ZonedDateTime newStart = MAY_20_2025_1PM.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime newEnd = MAY_20_2025_2PM.withZoneSameInstant(ZoneId.of("America/New_York"));
        EventUpdateDTO dto = createEventUpdateDTO(null, null, newStart, newEnd);

        Day day1 = createDayWithDate(newStart.toLocalDate(), creator);
        Set<Day> days = Set.of(day1);

        when(dayService.getOrCreateAllDaysBetween(
                newStart.withZoneSameInstant(ZoneOffset.UTC).toLocalDate(),
                newEnd.withZoneSameInstant(ZoneOffset.UTC).toLocalDate(),
                creator)).thenReturn(days);

        doNothing().when(eventValidator).validateStartBeforeEnd(any(), any());
        doNothing().when(eventValidator).validateNoConflicts(any(), any(), any(), any());

        EventPatchHandler.PatchResult result = EventPatchHandler.applyPatch(
                existingEvent, dto, eventValidator, dayService, creator);

        assertTrue(result.isUpdated());
        assertEquals(days, result.getNewDays());

        ZonedDateTime expectedStartUtc = ZonedDateTime.ofInstant(newStart.toInstant(), ZoneOffset.UTC);
        ZonedDateTime expectedEndUtc = ZonedDateTime.ofInstant(newEnd.toInstant(), ZoneOffset.UTC);

        assertEquals(expectedStartUtc, existingEvent.getStartTime());
        assertEquals(expectedEndUtc, existingEvent.getEndTime());
        assertEquals(newStart.getZone(), existingEvent.getTimezone());

        // Verify validator calls
        verify(eventValidator).validateStartBeforeEnd(eq(expectedStartUtc), eq(expectedEndUtc));
        verify(eventValidator).validateNoConflicts(eq(expectedStartUtc), eq(expectedEndUtc), eq(existingEvent.getId()), eq(day1));
        verify(dayService).getOrCreateAllDaysBetween(
                eq(expectedStartUtc.toLocalDate()),
                eq(expectedEndUtc.toLocalDate()),
                eq(creator)
        );
    }





    @Test
    void applyPatch_updatesMultipleFields() {
        ZonedDateTime newStart = MAY_20_2025_1PM;
        ZonedDateTime newEnd = MAY_20_2025_2PM;
        EventUpdateDTO dto = createEventUpdateDTO(EVENT_UPDATED, "New Desc", newStart, newEnd);

        Day day1 = createDayWithDate(newStart.toLocalDate(), creator);
        Set<Day> days = Collections.singleton(day1);
        when(dayService.getOrCreateAllDaysBetween(newStart.withZoneSameInstant(ZoneId.of("UTC")).toLocalDate(),
                newEnd.withZoneSameInstant(ZoneId.of("UTC")).toLocalDate(),
                creator)).thenReturn(days);

        doNothing().when(eventValidator).validateStartBeforeEnd(any(), any());
        doNothing().when(eventValidator).validateNoConflicts(any(), any(), any(), any());

        EventPatchHandler.PatchResult result = EventPatchHandler.applyPatch(existingEvent, dto, eventValidator, dayService, creator);

        assertTrue(result.isUpdated());
        assertEquals(EVENT_UPDATED, existingEvent.getName());
        assertEquals("New Desc", existingEvent.getDescription());
        assertEquals(days, result.getNewDays());

        // Verify validator called
        verify(eventValidator).validateStartBeforeEnd(any(), any());
        verify(eventValidator).validateNoConflicts(any(), any(), any(), any());
        verify(dayService).getOrCreateAllDaysBetween(any(), any(), any());
    }

    @Test
    void applyPatch_noChanges_returnsNotUpdated() {
        EventUpdateDTO dto = createEventUpdateDTO(null, null, null, null);

        EventPatchHandler.PatchResult result = EventPatchHandler.applyPatch(existingEvent, dto, eventValidator, dayService, creator);

        assertFalse(result.isUpdated());
        assertNull(result.getNewDays());

        verifyNoInteractions(eventValidator);
        verifyNoInteractions(dayService);
    }

    @Test
    void applyPatch_validateStartBeforeEndThrows_invalidTimeExceptionPropagated() {
        ZonedDateTime newStart = MAY_20_2025_1PM;
        ZonedDateTime newEnd = MAY_20_2025_2PM;
        EventUpdateDTO dto = createEventUpdateDTO(null, null, newStart, newEnd);

        when(dayService.getOrCreateAllDaysBetween(any(), any(), any())).thenReturn(Collections.emptySet());
        doThrow(new InvalidTimeException(newStart, newEnd)).when(eventValidator).validateStartBeforeEnd(any(), any());

        InvalidTimeException ex = assertThrows(InvalidTimeException.class, () ->
                EventPatchHandler.applyPatch(existingEvent, dto, eventValidator, dayService, creator));

        assertTrue(ex.getMessage().contains("Start time"));
        assertTrue(ex.getMessage().contains("2025-05-20 13:00:00 UTC"));
        assertTrue(ex.getMessage().contains("2025-05-20 14:00:00 UTC"));
    }


    @Test
    void applyPatch_validateNoConflictsThrows_conflictExceptionPropagated() {
        ZonedDateTime newStart = MAY_20_2025_1PM;
        ZonedDateTime newEnd = MAY_20_2025_2PM;
        EventUpdateDTO dto = createEventUpdateDTO(null, null, newStart, newEnd);

        Day day1 = createDayWithDate(newStart.toLocalDate(), creator);
        Set<Day> days = Collections.singleton(day1);
        when(dayService.getOrCreateAllDaysBetween(any(), any(), any())).thenReturn(days);

        doNothing().when(eventValidator).validateStartBeforeEnd(any(), any());
        doThrow(new ConflictException(existingEvent)).when(eventValidator).validateNoConflicts(any(), any(), any(), eq(day1));

        ConflictException ex = assertThrows(ConflictException.class, () ->
                EventPatchHandler.applyPatch(existingEvent, dto, eventValidator, dayService, creator));

        assertTrue(ex.getMessage().contains("Workout"));
        assertTrue(ex.getMessage().contains("2025-05-20 09:00:00 UTC"));
        assertTrue(ex.getMessage().contains("2025-05-20 11:00:00 UTC"));
    }

}
