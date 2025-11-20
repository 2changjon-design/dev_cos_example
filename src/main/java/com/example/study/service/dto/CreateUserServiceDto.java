package com.example.study.service.dto;


import com.example.study.controller.dto.CreateUserRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserServiceDto {
    private Long id;
    private String username;
    private String email;
    private String password;

}
