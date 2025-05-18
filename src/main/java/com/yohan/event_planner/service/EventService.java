package com.yohan.event_planner.service;

import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventResponseDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.model.Event;
import com.yohan.event_planner.model.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining operations related to {@link Event} management.
 * Provides methods for creating, retrieving, updating, deleting, and querying events
 * through data transfer objects (DTOs) and model entities.
 */
public interface EventService {

    /**
     * Retrieves a single event by its unique identifier.
     *
     * @param id the unique ID of the event
     * @return an {@link EventResponseDTO} representing the event details
     */
    EventResponseDTO getById(Long id);

    /**
     * Retrieves all events created by a specific user.
     *
     * @param userId the unique ID of the user (creator)
     * @return a list of {@link EventResponseDTO} objects for the user's events
     */
    List<EventResponseDTO> getByUserId(Long userId);

    /**
     * Retrieves all events associated with a particular day.
     *
     * @param dayId the unique ID of the day
     * @return a list of {@link EventResponseDTO} objects for the specified day
     */
    List<EventResponseDTO> getByDayId(Long dayId);

    /**
     * Retrieves all events occurring on a specific date.
     * This method typically considers events overlapping that date.
     *
     * @param date the {@link LocalDate} to query events for
     * @return a list of {@link EventResponseDTO} objects for the specified date
     */
    List<EventResponseDTO> getByDate(LocalDate date);

    /**
     * Creates a new event with the provided event creation data and creator.
     *
     * @param eventCreateDTO the data transfer object containing event creation details
     * @param creator the {@link User} who is creating the event
     * @return an {@link EventResponseDTO} representing the newly created event
     */
    EventResponseDTO createEvent(EventCreateDTO eventCreateDTO, User creator);

    /**
     * Updates an existing event identified by ID with the provided update data.
     *
     * @param id the unique ID of the event to update
     * @param eventUpdateDTO the data transfer object containing update details
     * @return an {@link EventResponseDTO} representing the updated event
     */
    EventResponseDTO updateEvent(Long id, EventUpdateDTO eventUpdateDTO);

    /**
     * Persists the given event entity.
     * This method can be used for saving or updating an event directly.
     *
     * @param event the {@link Event} entity to save
     * @return the saved {@link Event} entity
     */
    Event saveEvent(Event event);

    /**
     * Deletes the event with the specified unique identifier.
     *
     * @param id the unique ID of the event to delete
     */
    void deleteById(Long id);
}
