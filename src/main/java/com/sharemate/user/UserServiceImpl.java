package com.sharemate.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserMapper userMapper;
    private Map<Long, User> users;
    private Long nextId = 1L;

    public UserServiceImpl() {
        userMapper = new UserMapper();
        users = new HashMap<>();
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустым");
        }
        User user = new User(nextId, userDto.getName(), userDto.getEmail());
        users.put(user.getId(), user);
        nextId++;
        return userMapper.convertToDto(user);

    }

    @Override
    public void updateUser(Long userId, UserDto userDto) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

    }

    @Override
    public UserDto getUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }
        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> result = new ArrayList<>();
        for (User user : users.values()) {
            result.add(userMapper.convertToDto(user));
        }
        return result;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);

    }
}
