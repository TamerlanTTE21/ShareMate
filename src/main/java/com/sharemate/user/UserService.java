package com.sharemate.user;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto getUser(Long userId);

    List<UserDto> getAllUsers();

    void updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
