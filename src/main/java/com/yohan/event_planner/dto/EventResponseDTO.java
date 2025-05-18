package com.yohan.event_planner.dto;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Data Transfer Object representing an Event response.
 * Used to transfer event data from the backend to clients.
 *
 * @param id         the unique identifier of the event
 * @param name       the name/title of the event
 * @param startTime  the event start time with timezone information (stored in UTC)
 * @param endTime    the event end time with timezone information (stored in UTC)
 * @param creatorId  the unique identifier of the user who created the event
 * @param dayIds     the list of IDs representing the days associated with this event
 */
public record EventResponseDTO(
        Long id,
        String name,
        ZonedDateTime startTime,
        ZonedDateTime endTime,
        Long creatorId,
        List<Long> dayIds
) {}
