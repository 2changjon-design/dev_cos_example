package com.example.study.service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchCondition {
    String nameKeyword;
    String emailKeyword;
    LocalDateTime createdAfter;
    LocalDateTime createdBefore;
    Integer minPurchaseCount;
}
