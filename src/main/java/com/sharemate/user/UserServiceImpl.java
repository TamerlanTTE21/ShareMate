package com.sharemate.user;

import com.sharemate.exception.DuplicateEmailException;
import com.sharemate.exception.UserNotFoundException;
import com.sharemate.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final Map<Long, User> users;
    private Long nextId = 1L;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.users = new LinkedHashMap<>();
    }


    @Override
    public UserDto addUser(UserDto userDto) {

        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустым");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный формат email");
        }
        if (emailExists(userDto.getEmail(), null)) {
            throw new DuplicateEmailException("Пользователь с таким email уже существует");
        }
        User user = new User(nextId, userDto.getName(), userDto.getEmail());
        users.put(user.getId(), user);
        nextId++;
        return userMapper.convertToDto(user);

    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {

        User user = users.get(userId);

        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (emailExists(userDto.getEmail(), userId)) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
            user.setEmail(userDto.getEmail());
        }
        return userMapper.convertToDto(user);
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

    private boolean emailExists(String email, Long excludeUserId) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && !user.getId().equals(excludeUserId)) {
                return true;
            }
        }
        return false;
    }
}
