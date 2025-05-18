package com.yohan.event_planner.business.handler;

import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.Event;
import com.yohan.event_planner.validation.EventValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class responsible for applying partial updates (patches)
 * to existing {@link Event} entities based on {@link EventUpdateDTO}.
 * This class ONLY modifies the Event object in memory and validates input.
 * Persistence side effects must be handled separately.
 */
public class EventPatchHandler {
    private static final Logger logger = LoggerFactory.getLogger(EventPatchHandler.class);

    /**
     * Result object to hold patch results.
     */
    public static class PatchResult {
        private final boolean updated;
        private final Set<Day> newDays;

        public PatchResult(boolean updated, Set<Day> newDays) {
            this.updated = updated;
            this.newDays = newDays;
        }

        public boolean isUpdated() {
            return updated;
        }

        public Set<Day> getNewDays() {
            return newDays;
        }
    }

    /**
     * Applies patch updates from EventUpdateDTO to an existing Event.
     * Validates time fields and checks for conflicts.
     * Returns PatchResult containing whether the event was changed and new days for replacement.
     *
     * @param existingEvent The event to update
     * @param eventUpdateDTO DTO containing patch updates (nullable fields)
     * @param eventValidator Validator for business rules
     * @param dayService Service to fetch days related to the event
     * @param creator The user who owns the event (needed to get/create days)
     * @return PatchResult indicating if update occurred and new days set
     */
    public static PatchResult applyPatch(Event existingEvent, EventUpdateDTO eventUpdateDTO,
                                         EventValidator eventValidator,
                                         com.yohan.event_planner.service.DayService dayService,
                                         com.yohan.event_planner.model.User creator) {
        boolean isUpdated = false;
        Set<Day> newDays = null;

        // Name update
        if (eventUpdateDTO.name() != null && !eventUpdateDTO.name().equals(existingEvent.getName())) {
            existingEvent.setName(eventUpdateDTO.name());
            logger.info("Event name updated to: {}", eventUpdateDTO.name());
            isUpdated = true;
        }

        // Time update
        ZonedDateTime newStartInput = eventUpdateDTO.startTime() != null ? eventUpdateDTO.startTime() : existingEvent.getStartTime();
        ZonedDateTime newEndInput = eventUpdateDTO.endTime() != null ? eventUpdateDTO.endTime() : existingEvent.getEndTime();

        boolean startChanged = eventUpdateDTO.startTime() != null && !eventUpdateDTO.startTime().equals(existingEvent.getStartTime());
        boolean endChanged = eventUpdateDTO.endTime() != null && !eventUpdateDTO.endTime().equals(existingEvent.getEndTime());

        if (startChanged || endChanged) {
            if (newStartInput == null || newEndInput == null) {
                throw new IllegalArgumentException("Start time and end time must not be null when updating event times");
            }

            ZonedDateTime newStartUtc = newStartInput.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime newEndUtc = newEndInput.withZoneSameInstant(ZoneOffset.UTC);

            // Validate start < end (UTC)
            eventValidator.validateStartBeforeEnd(newStartUtc, newEndUtc);

            // Get or create days based on UTC local dates
            newDays = dayService.getOrCreateAllDaysBetween(newStartUtc.toLocalDate(), newEndUtc.toLocalDate(), creator);

            // Validate no conflicts (UTC)
            for (Day day : newDays) {
                eventValidator.validateNoConflicts(newStartUtc, newEndUtc, existingEvent.getId(), day);
            }

            // Update Event fields in memory only
            existingEvent.setStartTime(newStartUtc);
            existingEvent.setEndTime(newEndUtc);
            existingEvent.setTimezone(newStartInput.getZone());

            logger.info("Event start/end time updated to {} - {} (UTC), timezone set to {}",
                    newStartUtc, newEndUtc, newStartInput.getZone());
            isUpdated = true;
        }

        // Description update
        if (eventUpdateDTO.description() != null && !eventUpdateDTO.description().equals(existingEvent.getDescription())) {
            existingEvent.setDescription(eventUpdateDTO.description());
            logger.info("Event description updated");
            isUpdated = true;
        }

        return new PatchResult(isUpdated, newDays);
    }
}
