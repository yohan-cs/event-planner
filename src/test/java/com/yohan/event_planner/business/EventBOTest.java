package com.yohan.event_planner.business;

import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.Event;
import com.yohan.event_planner.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.yohan.event_planner.repository.DayRepository;
import com.yohan.event_planner.repository.EventRepository;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EventBOTest {

    private EventRepository eventRepository;
    private DayRepository dayRepository;
    private EventBO eventBO;

    private User dummyUser;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        dayRepository = mock(DayRepository.class);
        eventBO = new EventBO(eventRepository, dayRepository);

        dummyUser = new User("user1", "hash", "email@example.com", ZoneId.of("UTC"), "John", "Doe");
    }

    @Test
    void testFindById_returnsEvent() {
        ZonedDateTime start = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(2);
        Event event = new Event("Test Event", start, end, dummyUser);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Optional<Event> found = eventBO.getById(1L);

        assertTrue(found.isPresent());
        assertEquals("Test Event", found.get().getName());
        verify(eventRepository).findById(1L);
    }

    @Test
    void testFindByDayId_returnsList() {
        ZonedDateTime start = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(2);
        Event event = new Event("Day Event", start, end, dummyUser);

        when(eventRepository.findByDays_Id(10L)).thenReturn(List.of(event));

        List<Event> events = eventBO.getByDayId(10L);

        assertEquals(1, events.size());
        assertEquals("Day Event", events.get(0).getName());
        verify(eventRepository).findByDays_Id(10L);
    }

    @Test
    void testGetEventsByDate_returnsList() {
        LocalDate date = LocalDate.of(2025, 5, 14);
        ZonedDateTime start = date.atTime(9, 0).atZone(ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(1);
        Event event = new Event("Date Event", start, end, dummyUser);

        when(eventRepository.findByDate(date)).thenReturn(List.of(event));

        List<Event> events = eventBO.getEventsByDate(start);

        assertEquals(1, events.size());
        assertEquals("Date Event", events.get(0).getName());
        verify(eventRepository).findByDate(date);
    }

    @Test
    void testSaveEvent_callsRepository() {
        ZonedDateTime start = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(1);
        Event event = new Event("Save Event", start, end, dummyUser);

        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventBO.save(event);

        assertEquals("Save Event", saved.getName());
        verify(eventRepository).save(event);
    }

    @Test
    void testDeleteById_callsRepository() {
        doNothing().when(eventRepository).deleteById(1L);

        eventBO.deleteById(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    void testCreateEvent_createsAndSaves() {
        ZonedDateTime start = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(2);

        Event event = new Event("New Event", null, null, dummyUser);

        when(dayRepository.findByDate(any())).thenReturn(Optional.empty());
        when(dayRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Event created = eventBO.createEvent(event, start, end);

        assertEquals(start, created.getStartTime());
        assertEquals(end, created.getEndTime());
        assertEquals(ZoneId.of("UTC"), created.getTimezone());

        verify(eventRepository).save(created);
        verify(dayRepository).saveAll(any());
    }

    @Test
    void testUpdateEvent_updatesFields() {
        ZonedDateTime oldStart = ZonedDateTime.of(2025, 5, 14, 9, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime oldEnd = oldStart.plusHours(1);
        Event existing = new Event("Old Event", oldStart, oldEnd, dummyUser);

        ZonedDateTime newStart = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime newEnd = newStart.plusHours(2);

        Event updated = new Event("Updated Event", newStart, newEnd, dummyUser);
        updated.setDescription("Updated description");

        // Mock eventRepository to find existing event by ID
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));
        // Mock save to just return the passed event
        when(eventRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Prepare mock Day for the new event date(s)
        Day mockDay = new Day(newStart.toLocalDate().atStartOfDay(newStart.getZone()));

        // Mock dayRepository to find the Day by date, save, and saveAll methods
        when(dayRepository.findByDate(any())).thenReturn(Optional.of(mockDay));
        when(dayRepository.save(any())).thenReturn(mockDay);
        when(dayRepository.saveAll(any())).thenReturn(List.of(mockDay));

        // Call the method under test
        Event result = eventBO.updateEvent(1L, updated);

        // Assertions
        assertEquals("Updated Event", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(newStart, result.getStartTime());
        assertEquals(newEnd, result.getEndTime());

        // Verify interactions
        verify(eventRepository).findById(1L);
        verify(eventRepository).save(result);
        verify(dayRepository).saveAll(any());
    }


    @Test
    void testValidateStartBeforeEnd_throwsException() {
        ZonedDateTime start = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime end = start.minusHours(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            eventBO.createEvent(new Event("Invalid Event", null, null, dummyUser), start, end);
        });

        assertEquals("Start time must be before end time", ex.getMessage());
    }

    @Test
    void testValidateMatchingTimezone_throwsException() {
        ZonedDateTime start = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = ZonedDateTime.of(2025, 5, 14, 12, 0, 0, 0, ZoneId.of("America/New_York"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            eventBO.createEvent(new Event("Invalid TZ Event", null, null, dummyUser), start, end);
        });

        assertEquals("Start and end timezones must match", ex.getMessage());
    }

    @Test
    void testValidateNoConflicts_throwsException() {
        ZonedDateTime start = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusHours(2);

        // Existing event that overlaps with new event
        Event existingEvent = new Event("Existing Event", start.plusMinutes(30), end.plusHours(1), dummyUser);
        Day day = new Day(start.toLocalDate().atStartOfDay(ZoneId.of("UTC")));
        day.addEvent(existingEvent);

        // Mock dayRepository to return our day when findByDate is called with the date
        when(dayRepository.findByDate(start.toLocalDate())).thenReturn(Optional.of(day));

        // Mock save to just return the passed Day
        when(dayRepository.save(any(Day.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // We also mock eventRepository.save to just return the event, though it should throw before that
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            eventBO.createEvent(new Event("Conflict Event", null, null, dummyUser), start, end);
        });

        assertTrue(ex.getMessage().contains("conflicts"));
    }

}
