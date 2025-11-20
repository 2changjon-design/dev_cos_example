package com.example.study.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserRecentActivityDto {

    private final Long userId;
    private final String username;
    private final Long recentOrderCount;
    private final LocalDateTime lastOrderAt;

    @QueryProjection
    public UserRecentActivityDto(Long userId,
                                 String username,
                                 Long recentOrderCount,
                                 LocalDateTime lastOrderAt) {
        this.userId = userId;
        this.username = username;
        this.recentOrderCount = recentOrderCount == null ? 0L : recentOrderCount;
        this.lastOrderAt = lastOrderAt;
    }
}
