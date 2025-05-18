package com.yohan.event_planner.service;

import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service that prepares and ensures the existence of Day entities for a given date range,
 * used during event scheduling. Each Day is tied to a specific user.
 */
@Service
public class EventScheduleService {

    private final DayService dayService;

    public EventScheduleService(DayService dayService) {
        this.dayService = dayService;
    }

    /**
     * Retrieves or creates all days between start and end date (inclusive) for the given user.
     * Intended for use when scheduling an event.
     *
     * @param start   the start date of the range
     * @param end     the end date of the range
     * @param creator the user who owns the days
     * @return a set of all Day objects between the range, created or fetched from storage
     */
    public Set<Day> prepareEventDays(LocalDate start, LocalDate end, User creator) {
        Set<LocalDate> targetDates = new HashSet<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            targetDates.add(d);
        }

        List<Day> existingDays = dayService.getExistingDaysByDates(targetDates, creator);
        Set<LocalDate> foundDates = new HashSet<>();
        for (Day day : existingDays) {
            foundDates.add(day.getDate());
        }

        List<Day> missingDays = new ArrayList<>();
        for (LocalDate d : targetDates) {
            if (!foundDates.contains(d)) {
                missingDays.add(new Day(d, creator));
            }
        }

        if (!missingDays.isEmpty()) {
            existingDays.addAll(dayService.saveAllDays(missingDays));
        }

        return new HashSet<>(existingDays);
    }
}
