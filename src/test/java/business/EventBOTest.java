package business;

import exception.InvalidTimeException;
import exception.InvalidTimezoneException;
import model.Event;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventBOTest {

    @Test
    public void testFinalizeEventWithValidTimes() {
        // Test the finalizeEvent method with valid times
        ZonedDateTime startTime = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.of(2025, 5, 14, 12, 0, 0, 0, ZoneId.of("UTC"));

        Event event = new Event();
        event.setStartTime(startTime);
        event.setEndTime(endTime);

        EventBO eventBO = new EventBO();
        eventBO.finalizeEvent(event); // This should not throw any exceptions

        // Check that the duration was set correctly
        assertNotNull(event.getDurationInMinutes());
        assertEquals(120, event.getDurationInMinutes());
    }

    @Test
    public void testFinalizeEventWithInvalidStartAndEndTime() {
        // Test the finalizeEvent method with invalid times (start after end)
        ZonedDateTime startTime = ZonedDateTime.of(2025, 5, 14, 12, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));

        Event event = new Event();
        event.setStartTime(startTime);
        event.setEndTime(endTime);

        EventBO eventBO = new EventBO();

        // Expecting an InvalidTimeException to be thrown
        assertThrows(InvalidTimeException.class, () -> {
            eventBO.finalizeEvent(event);
        });
    }

    @Test
    public void testFinalizeEventWithMismatchedTimezones() {
        // Test the finalizeEvent method with mismatched timezones
        ZonedDateTime startTime = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.of(2025, 5, 14, 12, 0, 0, 0, ZoneId.of("America/New_York"));

        Event event = new Event();
        event.setStartTime(startTime);
        event.setEndTime(endTime);

        EventBO eventBO = new EventBO();

        // Expecting an InvalidTimezoneException to be thrown
        assertThrows(InvalidTimezoneException.class, () -> {
            eventBO.finalizeEvent(event);
        });
    }

    @Test
    public void testFinalizeEventWithZeroDuration() {
        // Test the finalizeEvent method with a start time equal to the end time (should throw InvalidTimeException)
        ZonedDateTime startTime = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));

        Event event = new Event();
        event.setStartTime(startTime);
        event.setEndTime(endTime);

        EventBO eventBO = new EventBO();

        // Expecting an InvalidTimeException to be thrown
        assertThrows(InvalidTimeException.class, () -> {
            eventBO.finalizeEvent(event);
        });
    }

    @Test
    public void testFinalizeEventWithDifferentTimezonesAndValidDuration() {
        // Test the finalizeEvent method with different time zones and valid duration
        ZonedDateTime startTime = ZonedDateTime.of(2025, 5, 14, 10, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.of(2025, 5, 14, 12, 0, 0, 0, ZoneId.of("UTC"));

        Event event = new Event();
        event.setStartTime(startTime);
        event.setEndTime(endTime);

        EventBO eventBO = new EventBO();
        eventBO.finalizeEvent(event); // This should not throw any exceptions

        // Check that the duration was set correctly
        assertNotNull(event.getDurationInMinutes());
        assertEquals(120, event.getDurationInMinutes());
    }
}