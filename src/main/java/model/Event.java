package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


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

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long durationInMinutes;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description; // Description of event

    @NotNull(message = "Timezone is required")
    @com.yohan.event_planner.validation.ValidZoneId(message = "Invalid timezone provided")
    @Column(nullable = false)
    private ZoneId timezone;

    public Event() {
    }

    public Event(String name, ZonedDateTime startTime, ZonedDateTime endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public void setDurationInMinutes(Long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public Long getDurationInMinutes() {
        return durationInMinutes;
    }

    public String getDescription() {
        return description;
    }
}
