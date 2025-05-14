package repository;

import model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DayRepository extends JpaRepository<Day, Long> {

    Optional<Day> findByDate(LocalDate date);
}