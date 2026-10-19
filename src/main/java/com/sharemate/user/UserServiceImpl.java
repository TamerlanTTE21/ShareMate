package com.sharemate.user;

import com.sharemate.exception.DuplicateEmailException;
import com.sharemate.exception.UserNotFoundException;
import com.sharemate.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
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
        User user = new User(null, userDto.getName(), userDto.getEmail());
        User savedUser = userRepository.save(user);
        return userMapper.convertToDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            if (emailExists(userDto.getEmail(), userId)) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
            user.setEmail(userDto.getEmail());
        }
        User savedUser = userRepository.save(user);
        return userMapper.convertToDto(savedUser);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(userMapper.convertToDto(user));
        }
        return result;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private boolean emailExists(String email, Long excludeUserId) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            return false;
        }
        return !existingUser.get().getId().equals(excludeUserId);
    }
}
