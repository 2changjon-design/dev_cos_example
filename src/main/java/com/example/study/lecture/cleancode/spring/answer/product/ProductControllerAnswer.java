package com.example.study.lecture.cleancode.spring.answer.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spring/answer/products")
public class ProductControllerAnswer {

    private final ProductServiceAnswer productService;

    public ProductControllerAnswer(ProductServiceAnswer productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> listProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PatchMapping("/{productId}/price")
    public ResponseEntity<ProductResponse> changePrice(
            @PathVariable Long productId,
            @RequestBody UpdateProductPriceRequest request
    ) {
        return ResponseEntity.ok(productService.changePrice(productId, request));
    }
}
