package com.yohan.event_planner.business;

import com.yohan.event_planner.business.handler.EventPatchHandler;
import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.exception.EventNotFoundException;
import com.yohan.event_planner.mapper.EventMapper;
import com.yohan.event_planner.domain.Day;
import com.yohan.event_planner.domain.Event;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.repository.EventRepository;
import com.yohan.event_planner.service.DayService;
import com.yohan.event_planner.service.EventScheduleService;
import com.yohan.event_planner.validation.EventValidator;
import com.yohan.event_planner.validation.utils.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/**
 * Business Object (Service) responsible for handling
 * event-related business logic, validation, and persistence.
 */
@Service
public class EventBO {

    private static final Logger logger = LoggerFactory.getLogger(EventBO.class);

    private final EventRepository eventRepository;
    private final DayService dayService;
    private final EventScheduleService eventScheduleService;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;

    /**
     * Constructs an EventBO with required dependencies.
     *
     * @param eventRepository       repository for Event persistence
     * @param dayService            service for Day entity management
     * @param eventScheduleService  service for event-day scheduling logic
     * @param eventValidator        validator for event business rules
     * @param eventMapper           mapper for converting DTOs to entities
     */
    public EventBO(EventRepository eventRepository, DayService dayService,
                   EventScheduleService eventScheduleService, EventValidator eventValidator,
                   EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.dayService = dayService;
        this.eventScheduleService = eventScheduleService;
        this.eventValidator = eventValidator;
        this.eventMapper = eventMapper;
    }

    @Transactional(readOnly = true)
    public Optional<Event> getById(Long eventId) {
        ValidationUtils.requireValidId(eventId, "Event ID");
        logger.debug("Fetching event with ID {}", eventId);
        return eventRepository.findById(eventId);
    }

    @Transactional(readOnly = true)
    public List<Event> getByDayId(Long dayId) {
        ValidationUtils.requireValidId(dayId, "Day ID");
        logger.debug("Fetching events for day ID {}", dayId);
        return eventRepository.findByDays_Id(dayId);
    }

    @Transactional(readOnly = true)
    public List<Event> getByCreatorId(Long creatorId) {
        ValidationUtils.requireValidId(creatorId, "Creator ID");
        logger.debug("Fetching events created by user ID {}", creatorId);
        return eventRepository.findByCreatorId(creatorId);
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByDate(LocalDate date, ZoneId userZone) {
        ZonedDateTime startOfDay = date.atStartOfDay(userZone);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        ZonedDateTime startOfDayUtc = startOfDay.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endOfDayUtc = endOfDay.withZoneSameInstant(ZoneOffset.UTC);

        logger.debug("Fetching events for date {} in zone {}, UTC range {} to {}",
                date, userZone, startOfDayUtc, endOfDayUtc);

        return eventRepository.findByDateRange(startOfDayUtc, endOfDayUtc);
    }

    @Transactional
    public Event save(Event event) {
        logger.debug("Saving event with ID {}", event.getId());
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteById(Long eventId) {
        ValidationUtils.requireValidId(eventId, "Event ID");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    logger.warn("Attempted to delete non-existent event with ID {}", eventId);
                    return new EventNotFoundException(eventId);
                });

        logger.info("Deleting event with ID {}", eventId);
        eventRepository.delete(event);
    }

    /**
     * Creates a new event based on the provided DTO and associates it with the creator.
     * Validates event timing and conflicts before persisting.
     *
     * @param dto     the event creation data transfer object; must be non-null and valid
     * @param creator the user creating the event; must be non-null
     * @return the newly created and persisted Event entity
     * @throws IllegalArgumentException if DTO or creator are invalid
     * @throws IllegalStateException    if event timing is invalid or conflicts exist
     */
    @Transactional
    public Event createEvent(EventCreateDTO dto, User creator) {
        ZonedDateTime startTimeUtc = dto.startTime().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endTimeUtc = dto.endTime().withZoneSameInstant(ZoneOffset.UTC);

        logger.info("Creating event '{}' for user ID {} from {} to {} (converted to UTC: {} to {})",
                dto.name(), creator.getId(), dto.startTime(), dto.endTime(), startTimeUtc, endTimeUtc);

        eventValidator.validateStartBeforeEnd(startTimeUtc, endTimeUtc);

        Set<Day> days = eventScheduleService.prepareEventDays(
                startTimeUtc.toLocalDate(),
                endTimeUtc.toLocalDate(),
                creator
        );

        for (Day day : days) {
            eventValidator.validateNoConflicts(startTimeUtc, endTimeUtc, null, day);
        }

        Event event = eventMapper.toEntity(dto, dto.startTime().getZone(), creator);

        // Explicitly set timezone from startTime's zone
        event.setTimezone(dto.startTime().getZone());

        for (Day day : days) {
            event.addDay(day);
        }

        Event saved = eventRepository.save(event);
        logger.info("Event '{}' created with ID {}", dto.name(), saved.getId());
        return saved;
    }

    /**
     * Updates an existing event with new data provided in the DTO.
     * Applies partial updates and manages associations with days.
     *
     * @param eventId        the ID of the event to update; must be non-null and positive
     * @param eventUpdateDTO the DTO containing fields to update; must be non-null
     * @return the updated and persisted Event entity
     * @throws EventNotFoundException  if no event with the given ID exists
     * @throws IllegalArgumentException if eventId or DTO are invalid
     */
    @Transactional
    public Event updateEvent(Long eventId, EventUpdateDTO eventUpdateDTO) {
        ValidationUtils.requireValidId(eventId, "Event ID");

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    logger.warn("Attempted to update non-existent event with ID {}", eventId);
                    return new EventNotFoundException(eventId);
                });

        logger.info("Updating event with ID {}", eventId);

        EventPatchHandler.PatchResult patchResult = EventPatchHandler.applyPatch(
                existingEvent,
                eventUpdateDTO,
                eventValidator,
                dayService,
                existingEvent.getCreator()
        );

        if (patchResult.isUpdated()) {
            logger.info("Event with ID {} updated; updating associated days", eventId);

            Set<Day> newDays = patchResult.getNewDays();

            if (newDays != null) {
                Set<Day> currentDays = new HashSet<>(existingEvent.getDays());

                Set<Day> daysToRemove = new HashSet<>(currentDays);
                daysToRemove.removeAll(newDays);
                for (Day dayToRemove : daysToRemove) {
                    existingEvent.removeDay(dayToRemove);
                }

                Set<Day> daysToAdd = new HashSet<>(newDays);
                daysToAdd.removeAll(currentDays);
                for (Day dayToAdd : daysToAdd) {
                    existingEvent.addDay(dayToAdd);
                }

                dayService.saveAllDays(newDays);
            }

            Event saved = eventRepository.save(existingEvent);
            logger.info("Event with ID {} saved after update", eventId);
            return saved;
        } else {
            logger.info("No changes detected for event ID {}; skipping update", eventId);
            return existingEvent;
        }
    }
}
