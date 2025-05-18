package com.yohan.event_planner.service;

import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing {@link Day} entities.
 * Each Day is user-specific and tied to a unique date.
 */
public interface DayService {

    /**
     * Retrieves a Day by its ID.
     *
     * @param id the ID of the Day
     * @return Optional containing the Day if found, otherwise empty
     */
    Optional<Day> getDayById(Long id);

    /**
     * Retrieves a Day by date and creator.
     *
     * @param date    the date of the Day
     * @param creator the owner of the Day
     * @return Optional containing the Day if found, otherwise empty
     */
    Optional<Day> getDayByDate(LocalDate date, User creator);

    /**
     * Retrieves or creates a Day for the given date and creator.
     *
     * @param date    the target date
     * @param creator the user who owns the Day
     * @return the existing or newly created Day
     */
    Day getOrCreateDay(LocalDate date, User creator);

    /**
     * Retrieves all existing Days that match the given dates and creator.
     *
     * @param dates   the dates to search for
     * @param creator the owner of the Days
     * @return a list of matching Days
     */
    List<Day> getExistingDaysByDates(Collection<LocalDate> dates, User creator);

    /**
     * Retrieves or creates all Days within the given date range for the specified creator.
     *
     * @param start   the start date (inclusive)
     * @param end     the end date (inclusive)
     * @param creator the owner of the Days
     * @return a set of all Days within the range
     */
    Set<Day> getOrCreateAllDaysBetween(LocalDate start, LocalDate end, User creator);

    /**
     * Saves a Day entity.
     *
     * @param day the Day to save
     * @return the saved Day
     */
    Day saveDay(Day day);

    /**
     * Saves a collection of Days.
     *
     * @param days the Days to save
     * @return list of saved Days
     */
    List<Day> saveAllDays(Collection<Day> days);

    /**
     * Deletes a Day by its ID.
     *
     * @param id the ID of the Day to delete
     */
    void deleteDay(Long id);
}
