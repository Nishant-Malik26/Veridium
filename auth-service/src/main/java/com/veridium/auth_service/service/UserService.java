package com.veridium.auth_service.service;

import com.veridium.auth_service.dto.UserCreationRequest;
import com.veridium.auth_service.dto.UserDto;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public final PasswordEncoder passwordEncoder;
    public final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserCreationRequest registrationRequest) {
        User user = new User(registrationRequest.first_name(), registrationRequest.last_name(), registrationRequest.email(), passwordEncoder.encode(registrationRequest.password()));
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getId(), savedUser.getEmail(), savedUser.getFirst_name(), savedUser.getLast_name());

    }
}
