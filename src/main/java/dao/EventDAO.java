package dao;

import model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventDAO extends JpaRepository<Event, Long> {
    List<Event> findByDayId(Long dayId);
    List<Event> findByDate(LocalDate date);
}