package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    @Autowired
    private final StatsService statsService;
    public final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    public void addHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Request to create entry: {}", endpointHitDto.toString());
        statsService.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam(name = "start") @DateTimeFormat(pattern = DATE_TIME) LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Processing get stats request");
        return statsService.getStats(start, end, uris, unique);
    }
}
