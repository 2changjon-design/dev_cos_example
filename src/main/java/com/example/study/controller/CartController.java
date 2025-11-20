package com.example.study.controller;

import com.example.study.controller.dto.AddCartItemRequest;
import com.example.study.controller.dto.CartItemResponse;
import com.example.study.controller.dto.CartSummaryResponse;
import com.example.study.controller.dto.RemoveCartItemRequest;
import com.example.study.service.CartService;
import com.example.study.service.dto.CartItem;
import com.example.study.service.dto.CartSummary;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public CartSummaryResponse addItem(@Valid @RequestBody AddCartItemRequest request) {
        CartSummary summary = cartService.addItem(request.itemName(), request.unitPrice(), request.quantity());
        return toResponse(summary);
    }

    @DeleteMapping("/items")
    public CartSummaryResponse removeItem(@Valid @RequestBody RemoveCartItemRequest request) {
        CartSummary summary = cartService.removeItem(request.itemName());
        return toResponse(summary);
    }

    @GetMapping("/summary")
    public CartSummaryResponse getSummary() {
        return toResponse(cartService.getSummary());
    }

    private CartSummaryResponse toResponse(CartSummary summary) {
        List<CartItemResponse> itemResponses = summary.items().stream()
                .map(this::toItemResponse)
                .toList();
        return new CartSummaryResponse(itemResponses, summary.totalQuantity(), summary.totalPrice());
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(item.itemName(), item.unitPrice(), item.quantity());
    }
}
