package repository;

import model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {

    // Find a Day by its LocalDate (assuming you have a date field on Day)
    Optional<Day> findByDate(LocalDate date);
}
