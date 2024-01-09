package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationUpdateRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(CompilationNewDto compilationNewDto);

    CompilationDto patch(Integer compId, CompilationUpdateRequest compilationUpdateRequest);

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Integer compId);

    void delete(Integer compId);
}
