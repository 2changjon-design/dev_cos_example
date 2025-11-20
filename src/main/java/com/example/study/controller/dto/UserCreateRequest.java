package com.example.study.controller.dto;

import com.example.study.service.dto.UserCreateCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank @Size(max = 50) String username,
        @Email @NotBlank @Size(max = 100) String email,
        @NotBlank @Size(min = 6, max = 255) String password
) {

    public UserCreateCommand toCommand() {
        return new UserCreateCommand(username.trim(), email.trim(), password);
    }
}

