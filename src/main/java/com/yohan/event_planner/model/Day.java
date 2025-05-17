package com.yohan.event_planner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a calendar day that can be associated with multiple events.
 * Each Day has a unique date and belongs to a specific creator (user).
 * Days can be archived to indicate they are no longer active.
 */
@Entity
@Table(name = "days", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "creator_id"})
})
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique date this Day represents.
     */
    @NotNull(message = "Date cannot be null")
    @Column(nullable = false, unique = true)
    private LocalDate date;

    /**
     * Optional description for the day.
     */
    @Column(length = 1000)
    @Size(max = 1000)
    private String description;

    /**
     * The user who created this day.
     * This is immutable after creation.
     */
    @NotNull(message = "Creator cannot be null")
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /**
     * Events associated with this day.
     * This is the inverse side of the many-to-many relation with Event.
     */
    @ManyToMany(mappedBy = "days", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Event> events = new HashSet<>();

    /**
     * Whether this day is archived (no longer active).
     */
    private boolean archived = false;

    /**
     * Default constructor required by JPA.
     */
    public Day() {
    }

    /**
     * Constructs a Day with a date and creator.
     *
     * @param date the unique date for this Day
     * @param creator the user who created this Day
     */
    public Day(LocalDate date, User creator) {
        this.date = date;
        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
