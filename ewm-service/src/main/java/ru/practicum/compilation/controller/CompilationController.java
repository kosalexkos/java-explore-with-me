package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;
    private final String path = "/{compId}";

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                       @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping(path)
    public CompilationDto getById(@PathVariable Integer compId) {
        return compilationService.getById(compId);
    }
}
