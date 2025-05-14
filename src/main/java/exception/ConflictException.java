package exception;

import model.Event;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConflictException extends RuntimeException {

    public ConflictException(Event existingEvent) {
        super("Event conflicts with an existing event: " + existingEvent.getName() +
                " (" + formatDateTime(existingEvent.getStartTime()) + " - " +
                formatDateTime(existingEvent.getEndTime()) + ")");
    }

    private static String formatDateTime(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return dateTime.format(formatter);
    }

}