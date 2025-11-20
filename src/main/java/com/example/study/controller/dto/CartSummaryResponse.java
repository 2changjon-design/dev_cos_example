package com.example.study.controller.dto;

import java.util.List;

public record CartSummaryResponse(List<CartItemResponse> items, int totalQuantity, int totalPrice) {
}
