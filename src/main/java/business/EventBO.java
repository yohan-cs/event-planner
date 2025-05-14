package business;

import dao.DayDAO;
import dao.EventDAO;

import exception.*;
import model.Day;
import model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;

public class EventBO {

    private static final Logger logger = LoggerFactory.getLogger(EventBO.class);
    private final EventDAO eventDAO;
    private final DayDAO dayDAO;

    public EventBO(EventDAO eventDAO, DayDAO dayDAO) {
        this.eventDAO = eventDAO;
        this.dayDAO = dayDAO;
    }

    public List<Event> getEventsByDate(ZonedDateTime dateTime) {
        // Convert ZonedDateTime to LocalDate
        LocalDate localDate = dateTime.toLocalDate();

        // Call the repository to get events by date
        return eventDAO.findByDate(localDate);
    }

    public Event createEvent(Event event, ZonedDateTime startTime, ZonedDateTime endTime) {
        // Validate start and end times
        validateMatchingTimezone(event);
        validateStartBeforeEnd(startTime, endTime);

        // Get or create the days for the event (for all the days the event spans)
        Set<Day> days = getOrCreateAllDaysBetween(startTime.toLocalDate(), endTime.toLocalDate());

        // Check for conflicts in each day before proceeding
        for (Day day : days) {
            validateNoConflicts(startTime, endTime, event.getId(), day);
        }

        // Set the event's days and associate the event with each day
        for (Day day : days) {
            event.addDay(day);    // Add the day to the event
            day.addEvent(event);  // Add the event to the day
        }

        // Save the event
        eventDAO.save(event);

        // Save all affected days if no conflict is found
        dayDAO.saveAll(days);

        // Return the newly created event
        return event;
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        logger.info("Updating event with ID: {}", id);
        Event event = eventDAO.findById(id).orElseThrow(() -> new EventNotFoundException(id));

        // Update event name
        if (updatedEvent.getName() != null) {
            if (updatedEvent.getName().isBlank()) {
                throw new IllegalArgumentException("Event name can not be blank.");
            }
            event.setName(updatedEvent.getName());
        }

        // Update event start or end time
        if (updatedEvent.getStartTime() != null || updatedEvent.getEndTime() != null) {
            ZonedDateTime newStart = updatedEvent.getStartTime() != null
                    ? updatedEvent.getStartTime().withZoneSameInstant(ZoneOffset.UTC)
                    : event.getStartTime();

            ZonedDateTime newEnd = updatedEvent.getEndTime() != null
                    ? updatedEvent.getEndTime().withZoneSameInstant(ZoneOffset.UTC)
                    : event.getEndTime();

            validateStartBeforeEnd(newStart, newEnd);

            event.setStartTime(newStart);
            event.setEndTime(newEnd);

            long duration = calculateDurationInMinutes(newStart, newEnd);
            event.setDurationInMinutes(duration);
            logger.info("Updated times and duration for event ID {}", id);

            // Get or create the days for the updated event (for all the days the event spans)
            Set<Day> days = getOrCreateAllDaysBetween(newStart.toLocalDate(), newEnd.toLocalDate());

            // Validate conflicts for each day the event will span
            for (Day day : days) {
                validateNoConflicts(newStart, newEnd, event.getId(), day);
            }

            // Set the event's days and associate the event with the days
            for (Day day : days) {
                event.addDay(day);
                day.addEvent(event);
            }

            // Revalidate which Day the event belongs to based on the new time
            LocalDate newEventDate = newStart.toLocalDate();
            Day newDay = getOrCreateDay(newEventDate);

            if (!days.contains(newDay)) {
                logger.info("Day has changed for event with ID: {}, adding to new day: {}",
                        event.getId(),
                        newDay.getDate().toString());

                event.addDay(newDay);
                newDay.addEvent(event);
            }

            logger.info("Saving days...");
            dayDAO.saveAll(days);
        }

        // Update event description
        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }

        logger.info("Saving event with ID: {}", event.getId());
        return eventDAO.save(event);
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

    private long calculateDurationInMinutes(ZonedDateTime startTime, ZonedDateTime endTime) {
        logger.info("Calculating duration");
        long duration = Duration.between(startTime, endTime).toMinutes();

        logger.debug("Duration is: {} minutes", duration);

        return duration;
    }

    // Core conflict-checking logic
    private void validateNoConflicts(ZonedDateTime startTime, ZonedDateTime endTime, Long excludeEventId, Day day) {
        for (Event existing : day.getEvents()) {
            // Skip the check for the current event if we're updating
            if (excludeEventId != null && Objects.equals(existing.getId(), excludeEventId)) {
                continue; // Skip checking against the current event during updates
            }

            // Check if the event overlaps with any existing event
            if (existing.getStartTime().isBefore(endTime) && existing.getEndTime().isAfter(startTime)) {
                logger.error("Conflict found with existing event: {}", existing.getName());
                throw new ConflictException(existing);
            }
        }

        logger.info("No conflicts found");
    }

    public Day getOrCreateDay(LocalDate eventDate) {
        // Try to find the existing day
        Optional<Day> existingDay = dayDAO.findByDate(eventDate);

        // If no existing day, create a new one and save it
        if (existingDay.isEmpty()) {
            // Create a new day at the start of the day in UTC
            ZonedDateTime startOfDayInUTC = eventDate.atStartOfDay(ZoneOffset.UTC);
            Day newDay = new Day(startOfDayInUTC);
            dayDAO.save(newDay);
            return newDay;
        }

        // If the day already exists, return it
        return existingDay.get();
    }

    private Set<Day> getOrCreateAllDaysBetween(LocalDate startDate, LocalDate endDate) {
        Set<Day> days = new HashSet<>();

        // Loop through each date from startDate to endDate, inclusive
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Find the day from the repository, or create it if it doesn't exist
            Day day = getOrCreateDay(date);
            days.add(day);
        }

        return days;
    }



}
