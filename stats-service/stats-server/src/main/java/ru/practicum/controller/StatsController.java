package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;
    private final String path = "/hit";
    private final String path1 = "/stats";

    @PostMapping(path)
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        statsService.addHit(endpointHitDto);
    }

    @GetMapping(path1)
    public List<ViewStats> getStats(
            @RequestParam(name = "start", required = false) @DateTimeFormat(pattern = DATE_TIME) LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
