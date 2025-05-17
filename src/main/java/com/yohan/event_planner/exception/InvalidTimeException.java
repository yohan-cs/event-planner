package com.yohan.event_planner.exception;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Exception thrown when event start and end times are invalid,
 * specifically when the start time is not before the end time.
 */
public class InvalidTimeException extends RuntimeException {

    /**
     * Constructs an InvalidTimeException with a detailed message showing the invalid start and end times.
     *
     * @param startTime the event start time
     * @param endTime   the event end time
     */
    public InvalidTimeException(ZonedDateTime startTime, ZonedDateTime endTime) {
        super("Invalid event time: Start time " + formatDateTime(startTime) +
                " must be before end time " + formatDateTime(endTime) + ".");
    }

    private static String formatDateTime(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return dateTime.format(formatter);
    }
}
