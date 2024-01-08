package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestPrivateController {
    private final ParticipationRequestService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Integer userId,
                                          @RequestParam Integer eventId) {
        return participationService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patch(@PathVariable Integer userId,
                                         @PathVariable Integer requestId) {
        return participationService.patch(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable Integer userId) {
        return participationService.getAll(userId);
    }
}
