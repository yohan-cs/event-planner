package com.yohan.event_planner.exception;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class InvalidTimeException extends RuntimeException {
    public InvalidTimeException(ZonedDateTime startTime, ZonedDateTime endTime) {
        super("Invalid event time: Start time " + formatDateTime(startTime) +
                " must be before end time " + formatDateTime(endTime) + ".");
    }

    private static String formatDateTime(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return dateTime.format(formatter);
    }
}
