package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationUpdateRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    @Autowired
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody CompilationNewDto compilationNewDto) {
        return compilationService.create(compilationNewDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patch(@PathVariable Integer compId,
                                @Valid @RequestBody CompilationUpdateRequest compilationUpdateRequest) {
        return compilationService.patch(compId, compilationUpdateRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer compId) {
        compilationService.delete(compId);
    }
}
