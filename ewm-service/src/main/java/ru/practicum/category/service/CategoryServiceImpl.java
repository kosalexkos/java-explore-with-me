package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = CategoryMapper.INSTANCE.fromDto(categoryDto);
        return CategoryMapper.INSTANCE.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto patch(Integer catId, CategoryDto categoryDto) {
        Category category = findById(catId);

        Optional.ofNullable(categoryDto.getName()).ifPresent(category::setName);

        return CategoryMapper.INSTANCE.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).getContent().stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Integer catId) {
        return CategoryMapper.INSTANCE.toDto(findById(catId));
    }

    @Override
    @Transactional
    public void delete(Integer catId) {
        findById(catId);
        categoryRepository.deleteById(catId);
    }

    private Category findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s not found", id)));
    }
}
