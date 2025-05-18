package com.yohan.event_planner.mapper;

import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventResponseDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.domain.Event;
import com.yohan.event_planner.domain.User;
import org.mapstruct.*;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper interface for converting between {@link Event} entities and various Event-related DTOs.
 * <p>
 * Utilizes MapStruct with Spring component model for automatic implementation generation.
 * Includes custom mapping logic for time zone conversions and selective updates.
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    /**
     * Converts an {@link Event} entity to an {@link EventResponseDTO}, converting
     * start and end times to the provided user's time zone.
     *
     * @param event    the Event entity to convert; may be null
     * @param userZone the ZoneId representing the user's time zone for time conversion
     * @return the converted EventResponseDTO, or null if the event parameter is null
     */
    default EventResponseDTO toDto(Event event, ZoneId userZone) {
        if (event == null) {
            return null;
        }

        List<Long> dayIds = event.getDays() == null ? List.of() :
                event.getDays().stream()
                        .map(day -> day.getId())
                        .collect(Collectors.toList());

        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                convertToZone(event.getStartTime(), userZone),
                convertToZone(event.getEndTime(), userZone),
                event.getCreator().getId(),
                dayIds
        );
    }

    /**
     * Converts a list of {@link Event} entities to a list of {@link EventResponseDTO}s,
     * applying the same time zone conversion to each event.
     *
     * @param events   list of Event entities; must not be null
     * @param userZone ZoneId for the user's time zone
     * @return list of EventResponseDTOs
     */
    default List<EventResponseDTO> toDtoList(List<Event> events, ZoneId userZone) {
        return events.stream()
                .map(event -> toDto(event, userZone))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing {@link Event} entity with non-null fields from an {@link EventUpdateDTO}.
     * Null-valued properties in the DTO are ignored, preventing overwriting existing values with null.
     *
     * @param event the target Event entity to update
     * @param dto   the source EventUpdateDTO containing update data
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Event event, EventUpdateDTO dto);

    /**
     * Converts a {@link ZonedDateTime} to the specified target {@link ZoneId} while preserving the exact instant.
     *
     * @param original   the original ZonedDateTime to convert; may be null
     * @param targetZone the target ZoneId for conversion; may be null
     * @return ZonedDateTime converted to the target zone, or original if any parameter is null
     */
    default ZonedDateTime convertToZone(ZonedDateTime original, ZoneId targetZone) {
        if (original == null || targetZone == null) {
            return original;
        }
        return original.withZoneSameInstant(targetZone);
    }

    /**
     * Converts an {@link EventCreateDTO} to an {@link Event} entity.
     * The event start and end times are normalized to UTC.
     * The creator of the event is assigned.
     * The event timezone is set explicitly.
     *
     * @param dto           the EventCreateDTO containing event creation data; may be null
     * @param eventTimezone the ZoneId representing the event's timezone
     * @param creator       the User who creates the event
     * @return the constructed Event entity or null if the DTO is null
     */
    default Event toEntity(EventCreateDTO dto, ZoneId eventTimezone, User creator) {
        if (dto == null) return null;

        Event event = new Event(
                dto.name(),
                dto.startTime().withZoneSameInstant(ZoneOffset.UTC),
                dto.endTime().withZoneSameInstant(ZoneOffset.UTC),
                creator
        );
        event.setDescription(dto.description());
        event.setTimezone(eventTimezone);

        return event;
    }
}
