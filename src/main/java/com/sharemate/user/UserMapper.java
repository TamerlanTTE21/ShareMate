package com.sharemate.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
