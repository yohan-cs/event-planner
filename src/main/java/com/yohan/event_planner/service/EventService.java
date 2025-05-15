package com.yohan.event_planner.service;

import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventResponseDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    EventResponseDTO getById(Long id);

    List<EventResponseDTO> getByUserId(Long userId);

    List<EventResponseDTO> getByDayId(Long dayId);

    List<EventResponseDTO> getByDate(LocalDate date);

    EventResponseDTO createEvent(EventCreateDTO eventCreateDTO);

    EventResponseDTO updateEvent(Long id, EventUpdateDTO eventUpdateDTO);

    void deleteById(Long id);
}