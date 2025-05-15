package com.yohan.event_planner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime date;

    @Column(length = 1000)
    @Size(max = 1000)
    private String description;

    @ManyToMany(mappedBy = "days", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Event> events = new HashSet<>();

    private boolean archived = false;


    public Day() {
    }

    public Day(ZonedDateTime date) {
        // Convert the date to UTC for consistent storage
        this.date = date.withZoneSameInstant(ZoneId.of("UTC"));
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date.withZoneSameInstant(ZoneId.of("UTC"));  // Ensure UTC time zone
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    // Utility method to add an event to the day
    public void addEvent(Event event) {
        events.add(event);
        event.getDays().add(this);
    }

    // Utility method to remove an event from the day
    public void removeEvent(Event event) {
        events.remove(event);
        event.getDays().remove(this);
    }
}
