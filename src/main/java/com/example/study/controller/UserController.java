package com.example.study.controller;

import com.example.study.common.ApiResponse;
import com.example.study.common.annotation.Loggable;
import com.example.study.controller.dto.UserCreateRequest;
import com.example.study.controller.dto.UserResponse;
import com.example.study.controller.dto.UserUpdateRequest;
import com.example.study.service.UserService;
import com.example.study.service.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Loggable
    @GetMapping
    public ApiResponse<List<UserResponse>> findAll() {
        return ApiResponse.success(UserResponse.from(userService.searchUser()));
    }

    @Loggable
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        UserDto created = userService.createUser(request.toCommand());
        return ApiResponse.created(UserResponse.from(created));
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> findById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        return ApiResponse.success(UserResponse.from(user));
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> update(@PathVariable Long userId,
                                            @Valid @RequestBody UserUpdateRequest request) {
        UserDto updated = userService.updateUser(userId, request.toCommand());
        return ApiResponse.success(UserResponse.from(updated));
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.success();
    }
}
