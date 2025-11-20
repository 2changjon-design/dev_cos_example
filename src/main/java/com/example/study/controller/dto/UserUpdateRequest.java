package com.example.study.controller.dto;

import com.example.study.service.dto.UserUpdateCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Size(max = 50) String username,
        @Email @NotBlank @Size(max = 100) String email
) {

    public UserUpdateCommand toCommand() {
        return new UserUpdateCommand(username.trim(), email.trim());
    }
}

