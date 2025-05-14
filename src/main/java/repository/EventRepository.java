package repository;

import model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDayId(Long dayId);

    @Query("SELECT e FROM Event e WHERE e.startTime::date = :date OR e.endTime::date = :date")
    List<Event> findByDate(@Param("date") LocalDate date);
}
