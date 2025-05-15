package service;

import model.Event;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Optional<Event> findById(Long id);

    List<Event> findByDayId(Long dayId);

    List<Event> findByDate(LocalDate date);

    Event save(Event event);

    Event createEvent(Event event, ZonedDateTime startTime, ZonedDateTime endTime);

    Event updateEvent(Long id, Event updatedEvent);

    void deleteById(Long id);
}
