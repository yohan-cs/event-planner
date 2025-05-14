package service;

import model.Event;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Optional<Event> findById(Long id);
    List<Event> findByDayId(Long dayId);
    List<Event> findByDate(LocalDate date);
    Event save(Event event);
    void deleteById(Long id);

}
