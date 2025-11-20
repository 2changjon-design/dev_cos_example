package com.example.study.controller;


import com.example.study.common.ApiResponse;
import com.example.study.controller.dto.ProductRequestDto;
import com.example.study.controller.dto.ProductResponseDto;
import com.example.study.controller.dto.ProductSummaryResponseDto;
import com.example.study.mapper.ProductMapper;
import com.example.study.service.ProductService;
import com.example.study.service.dto.ProductSearchCondition;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    // prefix = /api/products

    private final ProductService productService;
    private final ProductMapper productMapper;

    // 전체 상품 조회
    @GetMapping
    public ApiResponse<List<ProductResponseDto>> getAll() {
        return ApiResponse.success(productService.getAll());
    }

    // 단일 상품 조회
    @GetMapping("/{id}")
    public ApiResponse<ProductResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(productService.getById(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock
    ) {
        return ApiResponse.success(productService.search(
                buildSearchCondition(name, minPrice, maxPrice, categoryId, minStock, maxStock)
        ));
    }

    @GetMapping("/search/page")
    public ApiResponse<Page<ProductResponseDto>> searchProductsPage(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock
    ) {
        return ApiResponse.success(productService.search(
                buildSearchCondition(name, minPrice, maxPrice, categoryId, minStock, maxStock),
                pageable
        ));
    }

    @GetMapping("/categories/{categoryId}/summary")
    public ApiResponse<List<ProductSummaryResponseDto>> getCategorySummary(@PathVariable Long categoryId) {
        return ApiResponse.success(productService.getCategorySummaries(categoryId));
    }

    private ProductSearchCondition buildSearchCondition(String name,
                                                        BigDecimal minPrice,
                                                        BigDecimal maxPrice,
                                                        Long categoryId,
                                                        Integer minStock,
                                                        Integer maxStock) {
        return ProductSearchCondition.builder()
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoryId(categoryId)
                .minStock(minStock)
                .maxStock(maxStock)
                .build();
    }

    // 상품 생성
    // ProductRequestDto -> ProductServiceInputDto
    // ProductServiceInputDto -> Entity
    // Entity -> ProductServiceOutputDto
    // ProductServiceOutputDto -> ProductResponseDto
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(
            @Valid @RequestBody ProductRequestDto request
    ) {
        ProductResponseDto response = productService.create(productMapper.toService(request));
        return ApiResponse.created(response);
    }

    // 상품 수정
    @PutMapping("/{id}")
    public ApiResponse<ProductResponseDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody ProductRequestDto request) {
        return ApiResponse.success(productService.update(id, productMapper.toService(request)));
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
