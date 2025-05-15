package com.yohan.event_planner.business;

import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.yohan.event_planner.repository.DayRepository;
import com.yohan.event_planner.repository.EventRepository;

import java.time.*;
import java.util.*;

@Service  // Mark as service for Spring to inject it
public class EventBO {

    private static final Logger logger = LoggerFactory.getLogger(EventBO.class);

    private final EventRepository eventRepository;
    private final DayRepository dayRepository;

    public EventBO(EventRepository eventRepository, DayRepository dayRepository) {
        this.eventRepository = eventRepository;
        this.dayRepository = dayRepository;
    }

    // Find event by ID
    public Optional<Event> getById(Long id) {
        return eventRepository.findById(id);
    }

    // Find events by day ID
    public List<Event> getByDayId(Long dayId) {
        return eventRepository.findByDays_Id(dayId);
    }

    // Find events by Creator ID
    public List<Event> getByCreatorId(Long creatorId) {
        return eventRepository.findByCreatorId(creatorId);
    }

    // Find events by LocalDate (converted to ZonedDateTime inside)
    public List<Event> getEventsByDate(ZonedDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        return eventRepository.findByDate(date);
    }

    // Save event
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    // Delete event by ID
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    // Create event with business logic
    public Event createEvent(Event event, ZonedDateTime startTime, ZonedDateTime endTime) {
        validateStartBeforeEnd(startTime, endTime);
        validateMatchingTimezone(startTime, endTime);

        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setTimezone(startTime.getZone());

        Set<Day> days = getOrCreateAllDaysBetween(startTime.toLocalDate(), endTime.toLocalDate());

        for (Day day : days) {
            validateNoConflicts(startTime, endTime, null, day);
        }

        for (Day day : days) {
            event.addDay(day);
            day.addEvent(event);
        }

        eventRepository.save(event);
        dayRepository.saveAll(days);

        return event;
    }

    // Update event
    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (updatedEvent.getName() != null) {
            event.setName(updatedEvent.getName());
        }

        if (updatedEvent.getStartTime() != null || updatedEvent.getEndTime() != null) {
            ZonedDateTime newStart = updatedEvent.getStartTime() != null
                    ? updatedEvent.getStartTime()
                    : event.getStartTime();

            ZonedDateTime newEnd = updatedEvent.getEndTime() != null
                    ? updatedEvent.getEndTime()
                    : event.getEndTime();

            validateStartBeforeEnd(newStart, newEnd);
            validateMatchingTimezone(newStart, newEnd);

            event.setStartTime(newStart);
            event.setEndTime(newEnd);
            event.setTimezone(newStart.getZone());

            Set<Day> days = getOrCreateAllDaysBetween(newStart.toLocalDate(), newEnd.toLocalDate());

            for (Day day : days) {
                validateNoConflicts(newStart, newEnd, id, day);
            }

            for (Day day : days) {
                event.addDay(day);
                day.addEvent(event);
            }

            dayRepository.saveAll(days);
        }

        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }

        return eventRepository.save(event);
    }

    // Validation helpers

    private void validateStartBeforeEnd(ZonedDateTime start, ZonedDateTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private void validateMatchingTimezone(ZonedDateTime start, ZonedDateTime end) {
        if (!start.getZone().equals(end.getZone())) {
            throw new IllegalArgumentException("Start and end timezones must match");
        }
    }

    private void validateNoConflicts(ZonedDateTime startTime, ZonedDateTime endTime, Long excludeEventId, Day day) {
        for (Event existing : day.getEvents()) {
            if (excludeEventId != null && existing.getId().equals(excludeEventId)) {
                continue;
            }
            if (existing.getStartTime().isBefore(endTime) && existing.getEndTime().isAfter(startTime)) {
                throw new IllegalArgumentException("Event time conflicts with existing event: " + existing.getName());
            }
        }
    }

    // Day helper methods

    private Day getOrCreateDay(LocalDate date) {
        return dayRepository.findByDate(date).orElseGet(() -> {
            Day newDay = new Day(date.atStartOfDay(ZoneOffset.UTC));
            return dayRepository.save(newDay);
        });
    }

    private Set<Day> getOrCreateAllDaysBetween(LocalDate start, LocalDate end) {
        Set<Day> days = new HashSet<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            days.add(getOrCreateDay(date));
        }
        return days;
    }


}
