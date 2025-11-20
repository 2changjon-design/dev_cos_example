package com.example.study.controller.practice;

import com.example.study.common.ApiResponse;
import com.example.study.controller.dto.ProductResponseDto;
import com.example.study.mapper.ProductMapper;
import com.example.study.repository.practice.CategoryOrderQueryRepository;
import com.example.study.repository.practice.CategoryProductQueryRepository;
import com.example.study.repository.practice.ProductSearchPracticeRepository;
import com.example.study.repository.projection.CategoryOrderCountDto;
import com.example.study.repository.projection.CategoryProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 실습: QueryDSL 예제 전용 컨트롤러.
 */
@RestController
@RequestMapping("/api/practice")
@RequiredArgsConstructor
public class QueryDslPracticeController {

    private final CategoryProductQueryRepository categoryProductQueryRepository;
    private final CategoryOrderQueryRepository categoryOrderQueryRepository;
    private final ProductSearchPracticeRepository productSearchPracticeRepository;
    private final ProductMapper productMapper;

    /**
     * 실습 1: 카테고리별 상품 조회.
     */
    @GetMapping("/products/by-category")
    public ApiResponse<List<CategoryProductDto>> findProductsByCategory(
            @RequestParam(required = false) String categoryName
    ) {
        return ApiResponse.success(categoryProductQueryRepository.findCategoryProducts(categoryName));
    }

    /**
     * 실습 2: 카테고리별 주문 건수 집계.
     */
    @GetMapping("/categories/order-counts")
    public ApiResponse<List<CategoryOrderCountDto>> findCategoryOrderCounts() {
        return ApiResponse.success(categoryOrderQueryRepository.findCategoryOrderCounts());
    }

    /**
     * 실습 3: 상품 동적 검색.
     */
    @GetMapping("/products/search")
    public ApiResponse<List<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return ApiResponse.success(
                productMapper.toResponseList(
                        productSearchPracticeRepository.searchProducts(name, minPrice, maxPrice)
                )
        );
    }
}
