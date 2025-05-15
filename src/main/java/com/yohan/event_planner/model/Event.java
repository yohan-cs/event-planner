package com.yohan.event_planner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.yohan.event_planner.validation.ValidZoneId;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Column(nullable = false)
    private ZonedDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Column(nullable = false)
    private ZonedDateTime endTime;

    @ManyToMany
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
    @ValidZoneId(message = "Invalid timezone provided")
    @Column(nullable = false)
    private ZoneId timezone;

    public Event() {
    }

    public Event(String name, ZonedDateTime startTime, ZonedDateTime endTime, User creator) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.creator = creator;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public void addDay(Day day) {
        days.add(day);
        day.getEvents().add(this);
    }

    public void removeDay(Day day) {
        days.remove(day);
        day.getEvents().remove(this);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getCreator() {
        return creator;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public Set<Day> getDays() {
        return days;
    }

    public String getDescription() {
        return description;
    }

    public ZoneId getTimezone() {
        return timezone;
    }
}
