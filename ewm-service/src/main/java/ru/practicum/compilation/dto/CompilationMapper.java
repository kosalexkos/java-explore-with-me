package ru.practicum.compilation.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.model.Event;

import java.util.Set;

@Mapper(uses = EventMapper.class)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation fromDto(CompilationNewDto dto, Set<Event> events);

    CompilationDto toDto(Compilation entity);
}
