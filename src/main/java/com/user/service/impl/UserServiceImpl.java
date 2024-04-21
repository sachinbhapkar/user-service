package com.user.service.impl;

import com.user.dto.UserDto;
import com.user.model.User;
import com.user.repository.UserRepository;
import com.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Object loginApi(UserDto dto) {
        String rawPassword = dto.getPassword();
        String username = dto.getUsername();
        Optional<User> user = userRepository.findByUsername(username);
        Long userId = user.get().getId();
        if(userId != null){
            String encodedPass = user.get().getPassword();
            if(passwordEncoder.matches(rawPassword, encodedPass)){
                return Map.of("message","login successful");
            };
        }
        return Map.of("message","Login Failed");
    }
}
