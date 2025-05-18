package com.yohan.event_planner.service;

import com.yohan.event_planner.business.EventBO;
import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventResponseDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.exception.EventNotFoundException;
import com.yohan.event_planner.mapper.EventMapper;
import com.yohan.event_planner.domain.Event;
import com.yohan.event_planner.domain.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Implementation of {@link EventService}.
 * Delegates event operations to {@link EventBO} and maps entities to DTOs.
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventBO eventBO;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventBO eventBO, EventMapper eventMapper) {
        this.eventBO = eventBO;
        this.eventMapper = eventMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResponseDTO getById(Long eventId) {
        ZoneId userZone = getUserZone();
        return eventBO.getById(eventId)
                .map(event -> eventMapper.toDto(event, userZone))
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventResponseDTO> getByUserId(Long userId) {
        ZoneId userZone = getUserZone();
        List<Event> events = eventBO.getByCreatorId(userId);
        return eventMapper.toDtoList(events, userZone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventResponseDTO> getByDayId(Long dayId) {
        ZoneId userZone = getUserZone();
        List<Event> events = eventBO.getByDayId(dayId);
        return eventMapper.toDtoList(events, userZone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventResponseDTO> getByDate(LocalDate date) {
        ZoneId userZone = getUserZone();
        List<Event> events = eventBO.getEventsByDate(date, userZone);
        return eventMapper.toDtoList(events, userZone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResponseDTO createEvent(EventCreateDTO eventCreateDTO, User creator) {
        ZoneId zoneId = eventCreateDTO.startTime().getZone();
        Event savedEvent = eventBO.createEvent(eventCreateDTO, creator);
        return eventMapper.toDto(savedEvent, zoneId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResponseDTO updateEvent(Long eventId, EventUpdateDTO eventUpdateDTO) {
        ZoneId userZone = getUserZone();

        Event existingEvent = eventBO.getById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        eventMapper.updateEntity(existingEvent, eventUpdateDTO);

        Event updatedEvent = eventBO.updateEvent(eventId, eventUpdateDTO);
        return eventMapper.toDto(updatedEvent, userZone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event saveEvent(Event event) {
        return eventBO.save(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long eventId) {
        eventBO.getById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        eventBO.deleteById(eventId);
    }

    /**
     * Retrieves the timezone of the current user or defaults to system timezone.
     *
     * @return the ZoneId for the current user or system default if unavailable
     */
    private ZoneId getUserZone() {
        // TODO: Replace with real authentication and user retrieval logic
        return ZoneId.systemDefault();
    }
}
