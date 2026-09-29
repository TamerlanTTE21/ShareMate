package com.sharemate.user;

public class UserMapper {
    public UserDto convertToDto(User user) {
        return new UserDto(user.getName(), user.getEmail());
    }
}
