package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    public void addHit(EndpointHitDto endpointHitDto) {
        EndpointHit hit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        statsRepository.save(hit);
    }

    @Transactional(readOnly = true)
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new RuntimeException("Invalid date range OR 'start' or 'end' is null");
        }
        return (unique) ? statsRepository.findUniqueStats(start, end, uris) :
                statsRepository.findStats(start, end, uris);
    }
}
