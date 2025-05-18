package com.yohan.event_planner.dto;

import java.time.ZonedDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object used when creating a new Event.
 * Contains required and optional fields validated for input correctness.
 *
 * Note: Timezone is not provided explicitly; it will be derived from the startTime's timezone.
 *
 * @param name        the name/title of the event; must be non-blank and max 50 characters
 * @param startTime   the start time of the event; must not be null, includes timezone info
 * @param endTime     the end time of the event; must not be null, includes timezone info
 * @param description optional description of the event; max 255 characters
 */
public record EventCreateDTO(

        @NotBlank(message = "Event name cannot be blank")
        @Size(max = 50, message = "Event name must be less than 50 characters")
        String name,

        @NotNull(message = "Start time cannot be null")
        ZonedDateTime startTime,

        @NotNull(message = "End time cannot be null")
        ZonedDateTime endTime,

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description

) {}
