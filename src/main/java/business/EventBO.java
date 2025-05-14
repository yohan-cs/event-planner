package business;

import exception.InvalidTimeException;
import exception.InvalidTimezoneException;
import model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventBO {

    private static final Logger logger = LoggerFactory.getLogger(EventBO.class);

    public void finalizeEvent(Event event) {
        validateMatchingTimezone(event);
        validateStartBeforeEnd(event.getStartTime(), event.getEndTime());
        long duration = calculateDuration(event.getStartTime(), event.getEndTime());
        event.setDurationInMinutes(duration);
    }

    private void validateMatchingTimezone(Event event) {
        logger.info("Validating matching timezones");
        ZoneId startZone = event.getStartTime().getZone();
        ZoneId endZone = event.getEndTime().getZone();
        logger.debug("Start time timezone: {}, End time timezone: {}", startZone, endZone);
        if (!startZone.equals(endZone)) {
            throw new InvalidTimezoneException(startZone, endZone);
        }
        logger.info("Start and end time have the same timezone");
    }

    private void validateStartBeforeEnd(ZonedDateTime startTime, ZonedDateTime endTime) {
        logger.info("Validating start and end time");
        if (!startTime.isBefore(endTime)) {
            throw new InvalidTimeException(startTime, endTime);
        }
        logger.info("Start and end time are valid");
    }

    private long calculateDuration(ZonedDateTime startTime, ZonedDateTime endTime) {
        logger.info("Calculating duration");
        long duration = Duration.between(startTime, endTime).toMinutes();

        logger.debug("Duration is: {} minutes", duration);

        return duration;
    }

}
