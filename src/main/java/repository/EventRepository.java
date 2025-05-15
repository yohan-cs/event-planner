package repository;

import model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by Day ID (assuming Event has a Set<Day> days mapped)
    List<Event> findByDays_Id(Long dayId);

    // Custom query to find events where the startTime's date matches the given LocalDate
    @Query("SELECT e FROM Event e WHERE FUNCTION('DATE', e.startTime) = :date")
    List<Event> findByDate(@Param("date") LocalDate date);
}
