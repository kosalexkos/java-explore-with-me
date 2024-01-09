package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> get(List<Integer> ids, int from, int size);

    void delete(Integer userId);
}
