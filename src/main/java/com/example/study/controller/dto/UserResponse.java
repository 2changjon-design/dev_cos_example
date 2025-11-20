package com.example.study.controller.dto;

import com.example.study.service.dto.UserDto;

import java.util.List;

public record UserResponse(Long id, String username, String email, int purchaseCount) {

    public static UserResponse from(UserDto dto) {
        return new UserResponse(dto.getId(), dto.getUsername(), dto.getEmail(), dto.getPurchaseCount());
    }

    public static List<UserResponse> from(List<UserDto> dtos) {
        return dtos.stream()
                .map(UserResponse::from)
                .toList();
    }
}
