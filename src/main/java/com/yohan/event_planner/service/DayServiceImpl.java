package com.yohan.event_planner.service;

import com.yohan.event_planner.exception.DayNotFoundException;
import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.User;
import com.yohan.event_planner.repository.DayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Implementation of {@link DayService}.
 * Handles persistence and retrieval of Day entities scoped by user and date.
 */
@Service
public class DayServiceImpl implements DayService {

    private final DayRepository dayRepository;

    public DayServiceImpl(DayRepository dayRepository) {
        this.dayRepository = dayRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Day> getDayById(Long id) {
        return dayRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Day> getDayByDate(LocalDate date, User creator) {
        return dayRepository.findByDateAndCreator(date, creator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Day getOrCreateDay(LocalDate date, User creator) {
        return getDayByDate(date, creator)
                .orElseGet(() -> dayRepository.save(new Day(date, creator)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Day> getExistingDaysByDates(Collection<LocalDate> dates, User creator) {
        return dayRepository.findAllByDateInAndCreator(dates, creator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Day> getOrCreateAllDaysBetween(LocalDate start, LocalDate end, User creator) {
        Set<LocalDate> dateSet = new HashSet<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            dateSet.add(d);
        }

        List<Day> existingDays = getExistingDaysByDates(dateSet, creator);
        Set<LocalDate> foundDates = new HashSet<>();
        for (Day day : existingDays) {
            foundDates.add(day.getDate());
        }

        List<Day> missingDays = new ArrayList<>();
        for (LocalDate date : dateSet) {
            if (!foundDates.contains(date)) {
                missingDays.add(new Day(date, creator));
            }
        }

        if (!missingDays.isEmpty()) {
            existingDays.addAll(dayRepository.saveAll(missingDays));
        }

        return new HashSet<>(existingDays);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Day saveDay(Day day) {
        return dayRepository.save(day);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Day> saveAllDays(Collection<Day> days) {
        return dayRepository.saveAll(days);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDay(Long id) {
        if (!dayRepository.existsById(id)) {
            throw new DayNotFoundException(id);
        }
        dayRepository.deleteById(id);
    }
}
