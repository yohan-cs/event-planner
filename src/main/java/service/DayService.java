package service;

import model.Day;

import java.time.LocalDate;
import java.util.Optional;

public interface DayService {

    Optional<Day> getDayById(Long id);

    Optional<Day> getDayByDate(LocalDate date);

    Day saveDay(Day day);

    void deleteDay(Long id);
}
