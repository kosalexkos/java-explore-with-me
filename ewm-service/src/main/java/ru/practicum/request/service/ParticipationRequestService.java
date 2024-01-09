package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto create(Integer userId, Integer eventId);

    ParticipationRequestDto patch(Integer userId, Integer requestId);

    List<ParticipationRequestDto> getAll(Integer userId);
}
