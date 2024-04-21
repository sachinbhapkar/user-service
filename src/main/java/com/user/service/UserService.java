package com.user.service;

import com.user.dto.UserDto;
import com.user.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User saveUser(User user);

    void deleteUser(Long userId);

    Object loginApi(UserDto dto);


}
