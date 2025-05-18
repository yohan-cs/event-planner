package com.yohan.event_planner.service;

import com.yohan.event_planner.domain.Day;
import com.yohan.event_planner.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventScheduleServiceTest {

    private DayService dayService;
    private EventScheduleService eventScheduleService;
    private User user;

    @BeforeEach
    void setUp() {
        dayService = mock(DayService.class);
        eventScheduleService = new EventScheduleService(dayService);

        user = new User();
    }

    @Test
    void prepareEventDays_shouldReturnSingleSavedDay_whenStartEqualsEndAndDayMissing() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        Day savedDay = new Day(date, user);

        when(dayService.getExistingDaysByDates(Set.of(date), user)).thenReturn(new ArrayList<>());
        when(dayService.saveAllDays(any())).thenReturn(List.of(savedDay));

        Set<Day> result = eventScheduleService.prepareEventDays(date, date, user);

        assertEquals(1, result.size());
        assertTrue(result.contains(savedDay));
    }

    @Test
    void prepareEventDays_shouldReturnAllExistingDays_whenAllExist() {
        LocalDate start = LocalDate.of(2025, 5, 20);
        LocalDate end = LocalDate.of(2025, 5, 22);
        Day day1 = new Day(start, user);
        Day day2 = new Day(start.plusDays(1), user);
        Day day3 = new Day(end, user);
        List<Day> existingDays = new ArrayList<>(List.of(day1, day2, day3));

        when(dayService.getExistingDaysByDates(anySet(), eq(user))).thenReturn(existingDays);

        Set<Day> result = eventScheduleService.prepareEventDays(start, end, user);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(existingDays));
        verify(dayService, never()).saveAllDays(any());
    }

    @Test
    void prepareEventDays_shouldReturnMixedExistingAndSavedDays_whenSomeAreMissing() {
        LocalDate start = LocalDate.of(2025, 5, 20);
        LocalDate end = LocalDate.of(2025, 5, 22);
        Day existingDay = new Day(start, user);
        Day savedDay1 = new Day(start.plusDays(1), user);
        Day savedDay2 = new Day(end, user);

        when(dayService.getExistingDaysByDates(anySet(), eq(user)))
                .thenReturn(new ArrayList<>(List.of(existingDay)));
        when(dayService.saveAllDays(any()))
                .thenReturn(List.of(savedDay1, savedDay2));

        Set<Day> result = eventScheduleService.prepareEventDays(start, end, user);

        assertEquals(3, result.size());
        assertTrue(result.contains(existingDay));
        assertTrue(result.contains(savedDay1));
        assertTrue(result.contains(savedDay2));
    }

    @Test
    void prepareEventDays_shouldReturnEmptySet_whenStartAfterEnd() {
        LocalDate start = LocalDate.of(2025, 5, 22);
        LocalDate end = LocalDate.of(2025, 5, 20);

        Set<Day> result = eventScheduleService.prepareEventDays(start, end, user);

        assertTrue(result.isEmpty());
        verify(dayService).getExistingDaysByDates(eq(Collections.emptySet()), eq(user));
        verify(dayService, never()).saveAllDays(any());
    }
}
