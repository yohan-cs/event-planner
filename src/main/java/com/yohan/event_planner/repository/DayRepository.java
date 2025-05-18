package com.yohan.event_planner.repository;

import com.yohan.event_planner.domain.Day;
import com.yohan.event_planner.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for performing CRUD operations on {@link Day} entities.
 */
@Repository
public interface DayRepository extends JpaRepository<Day, Long> {

    /**
     * Finds a Day by its date and creator.
     *
     * @param date    the date to search for
     * @param creator the owner of the Day
     * @return an Optional Day if found
     */
    Optional<Day> findByDateAndCreator(LocalDate date, User creator);

    /**
     * Finds all Days for a given creator and collection of dates.
     *
     * @param dates   the dates to search
     * @param creator the owner of the Days
     * @return a list of matching Days
     */
    List<Day> findAllByDateInAndCreator(Collection<LocalDate> dates, User creator);
}
