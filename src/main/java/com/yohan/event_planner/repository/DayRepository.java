package com.yohan.event_planner.repository;

import com.yohan.event_planner.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {

    Optional<Day> findByDate(LocalDate date);
}
