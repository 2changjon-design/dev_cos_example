package com.example.study.controller.dto;

import com.example.study.service.dto.OrderCreateServiceDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreateRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    Long userId;

    @NotNull(message = "상품 ID는 필수입니다.")
    Long productId;

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
    Integer quantity;

    public OrderCreateServiceDto toServiceDto() {
        return OrderCreateServiceDto.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}

