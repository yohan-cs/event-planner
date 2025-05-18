package com.yohan.event_planner.business;

import com.yohan.event_planner.business.handler.EventPatchHandler;
import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.exception.EventNotFoundException;
import com.yohan.event_planner.mapper.EventMapper;
import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.Event;
import com.yohan.event_planner.model.User;
import com.yohan.event_planner.repository.EventRepository;
import com.yohan.event_planner.service.DayService;
import com.yohan.event_planner.service.EventScheduleService;
import com.yohan.event_planner.validation.EventValidator;
import com.yohan.event_planner.util.TestConstants;
import com.yohan.event_planner.util.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventBOTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private DayService dayService;

    @Mock
    private EventScheduleService eventScheduleService;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventPatchHandler eventPatchHandler; // Not injected but mocked in static method, will be stubbed differently

    @InjectMocks
    private EventBO eventBO;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestConstants.TEST_USER;
        TestUtils.setId(testUser, TestConstants.USER_ID_1);
    }

    // ----- getById -----

    @Test
    void getById_shouldReturnEvent_whenEventExists() {
        Event event = TestUtils.createEventWithId(TestConstants.EVENT_ID_1, TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM, TestConstants.MAY_20_2025_11AM, testUser);

        when(eventRepository.findById(TestConstants.EVENT_ID_1)).thenReturn(Optional.of(event));

        Optional<Event> found = eventBO.getById(TestConstants.EVENT_ID_1);

        assertTrue(found.isPresent());
        assertEquals(TestConstants.EVENT_ID_1, found.get().getId());
        verify(eventRepository).findById(TestConstants.EVENT_ID_1);
    }

    @Test
    void getById_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> eventBO.getById(null));
        verifyNoInteractions(eventRepository);
    }

    @Test
    void getById_shouldThrowException_whenIdIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> eventBO.getById(-1L));
        verifyNoInteractions(eventRepository);
    }

    // ----- getByDayId -----

    @Test
    void getByDayId_shouldReturnEventsList_whenDayExists() {
        List<Event> expectedEvents = List.of(
                TestUtils.createEventWithId(1L, "Event 1", TestConstants.MAY_20_2025_9AM, TestConstants.MAY_20_2025_11AM, testUser),
                TestUtils.createEventWithId(2L, "Event 2", TestConstants.MAY_20_2025_10AM, TestConstants.MAY_20_2025_NOON, testUser)
        );

        when(eventRepository.findByDays_Id(TestConstants.USER_ID_1)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventBO.getByDayId(TestConstants.USER_ID_1);

        assertThat(actualEvents).isEqualTo(expectedEvents);
        verify(eventRepository).findByDays_Id(TestConstants.USER_ID_1);
    }

    @Test
    void getByDayId_shouldThrowException_whenDayIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> eventBO.getByDayId(0L));
        verifyNoInteractions(eventRepository);
    }

    // ----- getByCreatorId -----

    @Test
    void getByCreatorId_shouldReturnEventsCreatedByUser() {
        List<Event> expectedEvents = List.of(
                TestUtils.createEventWithId(1L, "Event 1", TestConstants.MAY_20_2025_9AM, TestConstants.MAY_20_2025_11AM, testUser)
        );

        when(eventRepository.findByCreatorId(TestConstants.USER_ID_1)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventBO.getByCreatorId(TestConstants.USER_ID_1);

        assertThat(actualEvents).isEqualTo(expectedEvents);
        verify(eventRepository).findByCreatorId(TestConstants.USER_ID_1);
    }

    @Test
    void getByCreatorId_shouldThrowException_whenCreatorIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> eventBO.getByCreatorId(-5L));
        verifyNoInteractions(eventRepository);
    }

    // ----- getEventsByDate -----

    @Test
    void getEventsByDate_shouldReturnEventsWithinUtcRange() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        ZoneId zone = ZoneId.of("UTC");

        ZonedDateTime expectedStart = date.atStartOfDay(zone).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime expectedEnd = expectedStart.plusDays(1).minusNanos(1);

        List<Event> expectedEvents = List.of(
                TestUtils.createEventWithId(1L, "Event 1", expectedStart, expectedEnd, testUser)
        );

        when(eventRepository.findByDateRange(expectedStart, expectedEnd)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventBO.getEventsByDate(date, zone);

        assertThat(actualEvents).isEqualTo(expectedEvents);
        verify(eventRepository).findByDateRange(expectedStart, expectedEnd);
    }

    // ----- save -----

    @Test
    void save_shouldPersistAndReturnEvent() {
        Event event = TestUtils.createEventWithId(TestConstants.EVENT_ID_1, TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM, TestConstants.MAY_20_2025_11AM, testUser);

        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventBO.save(event);

        assertThat(saved).isSameAs(event);
        verify(eventRepository).save(event);
    }

    // ----- deleteById -----

    @Test
    void deleteById_shouldDeleteEvent_whenEventExists() {
        Event event = TestUtils.createEventWithId(TestConstants.EVENT_ID_1, TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM, TestConstants.MAY_20_2025_11AM, testUser);

        when(eventRepository.findById(TestConstants.EVENT_ID_1)).thenReturn(Optional.of(event));

        assertDoesNotThrow(() -> eventBO.deleteById(TestConstants.EVENT_ID_1));

        verify(eventRepository).findById(TestConstants.EVENT_ID_1);
        verify(eventRepository).delete(event);
    }

    @Test
    void deleteById_shouldThrowEventNotFoundException_whenEventDoesNotExist() {
        when(eventRepository.findById(TestConstants.EVENT_ID_1)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventBO.deleteById(TestConstants.EVENT_ID_1));

        verify(eventRepository).findById(TestConstants.EVENT_ID_1);
        verify(eventRepository, never()).delete(any());
    }

    @Test
    void deleteById_shouldThrowException_whenIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> eventBO.deleteById(0L));
        verifyNoInteractions(eventRepository);
    }

    // ----- createEvent -----

    @Test
    void createEvent_shouldValidateAndSaveNewEventSuccessfully() {
        EventCreateDTO dto = TestConstants.VALID_EVENT_CREATE_DTO;

        ZonedDateTime startUtc = dto.startTime().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endUtc = dto.endTime().withZoneSameInstant(ZoneOffset.UTC);

        Set<Day> preparedDays = Set.of(TestUtils.createDayWithDate(startUtc.toLocalDate(), testUser));
        when(eventScheduleService.prepareEventDays(startUtc.toLocalDate(), endUtc.toLocalDate(), testUser))
                .thenReturn(preparedDays);

        doNothing().when(eventValidator).validateStartBeforeEnd(startUtc, endUtc);

        // Use eq() and isNull() for raw values when mixing with matchers
        doNothing().when(eventValidator).validateNoConflicts(
                eq(startUtc), eq(endUtc), isNull(Long.class), any(Day.class));

        Event mappedEvent = TestUtils.createEventWithId(null, dto.name(), dto.startTime(), dto.endTime(), testUser);
        when(eventMapper.toEntity(dto, dto.startTime().getZone(), testUser)).thenReturn(mappedEvent);

        // Add preparedDays to savedEvent to match actual logic
        Event savedEvent = TestUtils.createEventWithId(TestConstants.EVENT_ID_1, dto.name(), dto.startTime(), dto.endTime(), testUser);
        for (Day day : preparedDays) {
            savedEvent.addDay(day);
        }

        when(eventRepository.save(mappedEvent)).thenReturn(savedEvent);

        Event result = eventBO.createEvent(dto, testUser);

        assertNotNull(result);
        assertEquals(TestConstants.EVENT_ID_1, result.getId());
        assertThat(result.getDays()).containsAll(preparedDays);
        assertEquals(dto.startTime().getZone(), result.getTimezone());

        verify(eventValidator).validateStartBeforeEnd(startUtc, endUtc);
        verify(eventValidator, times(preparedDays.size())).validateNoConflicts(
                eq(startUtc), eq(endUtc), isNull(Long.class), any(Day.class));
        verify(eventRepository).save(mappedEvent);
        verify(eventMapper).toEntity(dto, dto.startTime().getZone(), testUser);
        verify(eventScheduleService).prepareEventDays(startUtc.toLocalDate(), endUtc.toLocalDate(), testUser);
    }






    @Test
    void createEvent_shouldThrowException_whenStartAfterEnd() {
        EventCreateDTO dto = new EventCreateDTO(
                TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_11AM,
                TestConstants.MAY_20_2025_9AM,
                "Invalid times"
        );

        doThrow(new IllegalStateException("Start time after end time"))
                .when(eventValidator).validateStartBeforeEnd(any(), any());

        assertThrows(IllegalStateException.class, () -> eventBO.createEvent(dto, testUser));
    }

    // ----- updateEvent -----

    @Test
    void updateEvent_shouldApplyPatchAndSave_whenEventExistsAndPatchUpdates() {
        Long eventId = TestConstants.EVENT_ID_1;
        Event existing = TestUtils.createEventWithId(eventId, "Old Name", TestConstants.MAY_20_2025_9AM, TestConstants.MAY_20_2025_11AM, testUser);
        EventUpdateDTO updateDTO = TestUtils.createEventUpdateDTO(
                "New Name", "Updated",
                TestConstants.MAY_20_2025_10AM,
                TestConstants.MAY_20_2025_NOON
        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existing));

        // Stub only the method that is actually called
        doNothing().when(eventValidator).validateStartBeforeEnd(any(), any());

        // Stub dayService if needed
        when(dayService.getOrCreateAllDaysBetween(any(), any(), eq(testUser)))
                .thenReturn(Set.of());

        eventBO.updateEvent(eventId, updateDTO);

        verify(eventRepository).save(existing);
        verify(eventValidator).validateStartBeforeEnd(any(), any());
        verify(dayService).getOrCreateAllDaysBetween(any(), any(), eq(testUser));
    }



    @Test
    void updateEvent_shouldSkipSave_whenPatchMakesNoChanges() {
        Long eventId = TestConstants.EVENT_ID_1;
        Event existing = TestUtils.createEventWithId(eventId, "No Change", TestConstants.MAY_20_2025_9AM, TestConstants.MAY_20_2025_11AM, testUser);
        existing.setDescription("Some description");

        EventUpdateDTO noChange = new EventUpdateDTO("No Change", "Some description", null, null);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existing));

        eventBO.updateEvent(eventId, noChange);

        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_shouldThrow_whenEventNotFound() {
        when(eventRepository.findById(TestConstants.EVENT_ID_1)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () ->
                eventBO.updateEvent(TestConstants.EVENT_ID_1, TestConstants.VALID_EVENT_UPDATE_DTO));
    }

}