package com.yohan.event_planner.exception;

import com.yohan.event_planner.model.Event;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Exception thrown when a new or updated event conflicts with an existing event's time range.
 * Contains details about the conflicting event for easier debugging.
 */
public class ConflictException extends RuntimeException {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    public ConflictException(Event existingEvent) {
        super(buildMessage(existingEvent));
    }

    private static String buildMessage(Event existingEvent) {
        return "Event conflicts with existing event (ID: " + existingEvent.getId() + "): " +
                existingEvent.getName() + " (" +
                formatDateTime(existingEvent.getStartTime()) + " - " +
                formatDateTime(existingEvent.getEndTime()) + ")";
    }

    private static String formatDateTime(ZonedDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
