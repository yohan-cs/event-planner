package com.yohan.event_planner.controller;

import com.yohan.event_planner.controller.EventController;
import com.yohan.event_planner.dto.EventResponseDTO;
import com.yohan.event_planner.exception.EventNotFoundException;
import com.yohan.event_planner.exception.GlobalExceptionHandler;
import com.yohan.event_planner.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .setControllerAdvice(new GlobalExceptionHandler())  // register your exception handler
                .build();
    }

    @Test
    void getById_existingEvent_returnsEventResponse() throws Exception {
        Long eventId = 1L;
        EventResponseDTO responseDTO = new EventResponseDTO(
                eventId,
                "Test Event",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(2),
                100L,
                null
        );

        when(eventService.getById(eventId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/events/{id}", eventId)   // <== note /api prefix here
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void getById_nonExistingEvent_returnsNotFound() throws Exception {
        Long eventId = 999L;
        when(eventService.getById(eventId)).thenThrow(new EventNotFoundException(eventId));

        mockMvc.perform(get("/api/events/{id}", eventId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event with ID " + eventId + " not found"));
    }
}
