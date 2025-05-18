package com.yohan.event_planner.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an event with a name, start and end times, associated days, description,
 * creator user, and timezone information.
 *
 * Timezone handling:
 *
 *   The event's {@code timezone} always reflects the timezone of the {@code startTime}.
 *   This allows {@code startTime} and {@code endTime} to have different time zones,
 *       which is useful for events like flights where departure and arrival zones differ.
 *   When {@link #setStartTime(ZonedDateTime)} is called, it updates both the {@code startTime}
 *       and the event's {@code timezone} accordingly.
 *
 */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event name cannot be blank")
    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @NotNull(message = "Start time cannot be null")
    @Column(nullable = false)
    private ZonedDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @Column(nullable = false)
    private ZonedDateTime endTime;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "event_day",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "day_id")
    )
    private Set<Day> days = new HashSet<>();

    @Column(length = 255)
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    @NotNull(message = "Timezone is required")
    @Column(nullable = false)
    private ZoneId timezone;

    /**
     * Default constructor for JPA.
     */
    public Event() {
    }

    /**
     * Constructs a new Event.
     *
     * @param name      the event name
     * @param startTime the start time with timezone; also sets the event's timezone
     * @param endTime   the end time with timezone (may differ from startTime's timezone)
     * @param creator   the user who created this event
     */
    public Event(String name, ZonedDateTime startTime, ZonedDateTime endTime, User creator) {
        this.name = name;
        this.setStartTime(startTime); // sets startTime and updates timezone
        this.endTime = endTime;
        this.creator = creator;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreator() {
        return creator;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the event.
     * Also updates the event's timezone to match the start time's timezone.
     *
     * @param startTime the new start time with timezone
     */
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        if (startTime != null) {
            this.timezone = startTime.getZone();
        }
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public Set<Day> getDays() {
        return days;
    }

    /**
     * Adds a Day association to this event.
     *
     * @param day the Day to associate with this event
     */
    public void addDay(Day day) {
        if (days.add(day)) {
            day.getEvents().add(this);
        }
    }

    /**
     * Removes a Day association from this event.
     *
     * @param day the Day to remove association with
     */
    public void removeDay(Day day) {
        if (days.remove(day)) {
            day.getEvents().remove(this);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZoneId getTimezone() {
        return timezone;
    }

    /**
     * Sets the timezone explicitly.
     * Normally managed automatically by the start time setter.
     *
     * @param timezone the timezone to set
     */
    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }
}
