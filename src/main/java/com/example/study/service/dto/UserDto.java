package com.example.study.service.dto;

import com.example.study.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private int purchaseCount;
    // 다른 데이터
    private int a;
    private int b; // ....

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .purchaseCount(user.getPurchases().size())
                .a(0)
                .b(0)
                .build();
    }
}
