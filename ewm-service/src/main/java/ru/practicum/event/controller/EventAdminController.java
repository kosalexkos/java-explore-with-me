package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventState;
import ru.practicum.event.dto.update_request.EventUpdateAdminRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.service.EventService;
import ru.practicum.dateConstraints.DateConstants;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    @Autowired
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto patch(@PathVariable Integer eventId,
                              @Valid @RequestBody EventUpdateAdminRequest updateEventAdminRequest) {
        return eventService.patchByAdmin(eventId, updateEventAdminRequest);
    }

    @GetMapping
    public List<EventFullDto> getAllByFilter(@RequestParam(required = false) List<Integer> users,
                                             @RequestParam(required = false) List<EventState> states,
                                             @RequestParam(required = false) List<Integer> categories,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = DateConstants.DATE_FORMAT) LocalDateTime rangeStart,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = DateConstants.DATE_FORMAT) LocalDateTime rangeEnd,
                                             @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        return eventService.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
