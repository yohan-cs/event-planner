package com.yohan.event_planner.repository;

import com.yohan.event_planner.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link Event} entities.
 * Extends Spring Data JPA's {@link JpaRepository} to provide standard CRUD operations
 * and declares custom queries for retrieving events based on specific criteria.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Finds all events associated with the given Day ID.
     *
     * This leverages the many-to-many relationship between Event and Day,
     * assuming Event has a {@code Set<Day> days} field.
     *
     * @param dayId the ID of the Day entity
     * @return a list of Events linked to the specified Day ID
     */
    List<Event> findByDays_Id(Long dayId);

    /**
     * Finds all events created by the user with the given creator ID.
     *
     * @param creatorId the ID of the User who created the events
     * @return a list of Events created by the specified user
     */
    List<Event> findByCreatorId(Long creatorId);

    /**
     * Finds all events that overlap with a given date range.
     *
     * This query returns events where the event's start time is before the end of the date range
     * and the event's end time is after the start of the date range, effectively finding
     * events that occur at least partially within the given range.
     *
     * @param startOfDayUtc the start of the date range (inclusive), as a ZonedDateTime in UTC
     * @param endOfDayUtc   the end of the date range (exclusive), as a ZonedDateTime in UTC
     * @return a list of Events overlapping with the specified date range
     */
    @Query("SELECT e FROM Event e WHERE e.startTime < :endOfDayUtc AND e.endTime > :startOfDayUtc")
    List<Event> findByDateRange(@Param("startOfDayUtc") ZonedDateTime startOfDayUtc,
                                @Param("endOfDayUtc") ZonedDateTime endOfDayUtc);
}
