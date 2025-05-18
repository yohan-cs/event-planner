package com.yohan.event_planner.validation;

import com.yohan.event_planner.exception.ConflictException;
import com.yohan.event_planner.exception.InvalidTimeException;
import com.yohan.event_planner.domain.Day;
import com.yohan.event_planner.domain.Event;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Validates event-related business rules such as time ordering and schedule conflicts.
 * <p>
 * Used to enforce that event start times are before end times and that no overlapping
 * events occur on the same day.
 */
@Component
public class EventValidator {

    /**
     * Validates that the event's start time occurs strictly before its end time.
     *
     * @param start the start time of the event
     * @param end   the end time of the event
     * @throws InvalidTimeException if the start time is not before the end time
     */
    public void validateStartBeforeEnd(ZonedDateTime start, ZonedDateTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidTimeException(start, end);
        }
    }

    /**
     * Validates that the proposed event time interval does not conflict with existing events
     * on the specified day, excluding an event by ID if provided (useful for updates).
     *
     * @param startTime      the proposed event start time
     * @param endTime        the proposed event end time
     * @param excludeEventId the event ID to exclude from conflict checking (may be null)
     * @param day            the {@link Day} whose events are checked for conflicts
     * @throws ConflictException if there is a scheduling conflict with another event
     */
    public void validateNoConflicts(ZonedDateTime startTime, ZonedDateTime endTime, Long excludeEventId, Day day) {
        for (Event existing : day.getEvents()) {
            // Skip the event being updated (if any)
            if (excludeEventId != null && existing.getId().equals(excludeEventId)) {
                continue;
            }
            // Check for overlap: existing start < new end AND existing end > new start
            if (existing.getStartTime().isBefore(endTime) && existing.getEndTime().isAfter(startTime)) {
                throw new ConflictException(existing);
            }
        }
    }
}
