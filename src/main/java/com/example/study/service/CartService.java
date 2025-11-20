package com.example.study.service;

import com.example.study.service.dto.CartItem;
import com.example.study.service.dto.CartSummary;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final Map<String, CartItem> cartItems = new LinkedHashMap<>();

    public synchronized CartSummary addItem(String itemName, int unitPrice, int quantity) {
        validateItemInput(itemName, unitPrice, quantity);
        CartItem existing = cartItems.get(itemName);
        if (existing == null) {
            cartItems.put(itemName, new CartItem(itemName, unitPrice, quantity));
        } else {
            cartItems.put(itemName, new CartItem(itemName, unitPrice, existing.quantity() + quantity));
        }
        return buildSummary();
    }

    public synchronized CartSummary removeItem(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        CartItem removed = cartItems.remove(itemName);
        if (removed == null) {
            throw new IllegalArgumentException("Item not found in cart");
        }
        return buildSummary();
    }

    public synchronized CartSummary getSummary() {
        return buildSummary();
    }

    private void validateItemInput(String itemName, int unitPrice, int quantity) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    private CartSummary buildSummary() {
        List<CartItem> items = new ArrayList<>(cartItems.values());
        int totalQuantity = items.stream().mapToInt(CartItem::quantity).sum();
        int totalPrice = items.stream().mapToInt(item -> item.unitPrice() * item.quantity()).sum();
        return new CartSummary(items, totalQuantity, totalPrice);
    }
}
