package com.yohan.event_planner.controller;

import com.yohan.event_planner.domain.PasswordVO;
import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventResponseDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;

import com.yohan.event_planner.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.yohan.event_planner.service.EventService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * REST controller for managing event-related endpoints.
 * Provides CRUD operations for events including creation, retrieval,
 * update, deletion, and querying events by user, day, or date.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    /**
     * Constructs the EventController with required EventService.
     *
     * @param eventService the service layer for event operations
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Creates a new event based on the provided EventCreateDTO.
     *
     * Note: Currently uses a temporary dummy user. This will be replaced
     * by authenticated user information once security is implemented.
     *
     * @param eventCreateDTO the DTO containing event creation data
     * @return ResponseEntity containing the created EventResponseDTO and HTTP status 201 Created
     */
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventCreateDTO eventCreateDTO) {
        EventResponseDTO response = eventService.createEvent(eventCreateDTO, getTemporaryDummyUser());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the ID of the event to retrieve
     * @return ResponseEntity containing the EventResponseDTO and HTTP status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id) {
        EventResponseDTO response = eventService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Applies partial updates to an existing event identified by its ID.
     *
     * @param id the ID of the event to update
     * @param updateDTO the DTO containing fields to update (partial)
     * @return ResponseEntity containing the updated EventResponseDTO and HTTP status 200 OK
     */
    @PatchMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateDTO updateDTO
    ) {
        EventResponseDTO response = eventService.updateEvent(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes the event identified by its ID.
     *
     * @param id the ID of the event to delete
     * @return ResponseEntity with HTTP status 204 No Content upon successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all events created by a specific user.
     *
     * @param userId the ID of the user whose events to retrieve
     * @return ResponseEntity containing a list of EventResponseDTOs and HTTP status 200 OK
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getMyEvents(@PathVariable Long userId) {
        List<EventResponseDTO> events = eventService.getByUserId(userId);
        return ResponseEntity.ok(events);
    }

    /**
     * Retrieves all events associated with a specific day.
     *
     * @param dayId the ID of the day to filter events by
     * @return ResponseEntity containing a list of EventResponseDTOs and HTTP status 200 OK
     */
    @GetMapping("/day/{dayId}")
    public ResponseEntity<List<EventResponseDTO>> getEventsByDay(@PathVariable Long dayId) {
        List<EventResponseDTO> events = eventService.getByDayId(dayId);
        return ResponseEntity.ok(events);
    }

    /**
     * Retrieves all events occurring on a specific date.
     *
     * @param dateIso ISO-8601 formatted date string (e.g., "2025-05-16")
     * @return ResponseEntity containing a list of EventResponseDTOs and HTTP status 200 OK,
     * or HTTP 400 Bad Request if the date format is invalid
     */
    @GetMapping("/date")
    public ResponseEntity<List<EventResponseDTO>> getEventsByDate(@RequestParam("date") String dateIso) {
        try {
            LocalDate date = LocalDate.parse(dateIso);
            List<EventResponseDTO> events = eventService.getByDate(date);
            return ResponseEntity.ok(events);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Temporary helper method to return a dummy user.
     * This should be replaced by the actual authenticated user
     * once JWT-based authentication is implemented.
     *
     * @return a dummy User instance for testing purposes
     */
    private User getTemporaryDummyUser() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        PasswordVO passwordVO = new PasswordVO("dummypassword", encoder);
        return new User(
                "dummy",
                passwordVO,
                "dummy@email.com",
                ZoneId.of("UTC"),
                "Dummy",
                "Smith"
        );
    }
}
