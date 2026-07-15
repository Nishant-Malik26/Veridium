package com.veridium.auth_service.controller;

import com.veridium.auth_service.dto.ApiResponse;
import com.veridium.auth_service.dto.UserCreationRequest;
import com.veridium.auth_service.dto.UserDto;
import com.veridium.auth_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller("auth/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody UserCreationRequest userCreationRequest) {
        UserDto user = userService.createUser(userCreationRequest);

        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                                                   .success(true)
                                                   .message("User created successfully.")
                                                   .data(user)
                                                   .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(response);
    }
}
