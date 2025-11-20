package com.example.study.service;


import com.example.study.common.ServiceException;
import com.example.study.common.ServiceExceptionCode;
import com.example.study.controller.dto.ProductResponseDto;
import com.example.study.controller.dto.ProductSummaryResponseDto;
import com.example.study.entity.Category;
import com.example.study.entity.Product;
import com.example.study.mapper.ProductMapper;
import com.example.study.repository.CategoryRepository;
import com.example.study.repository.ProductQueryRepository;
import com.example.study.repository.ProductRepository;
import com.example.study.service.dto.ProductServiceInputDto;
import com.example.study.service.dto.ProductSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getById(Long id) {
        return productMapper.toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponseDto create(ProductServiceInputDto input) {
        validatePrice(input.getPrice());
        validateStock(input.getStock());
        validateDuplicateName(input.getName());

        Category category = findCategory(input.getCategoryId());

        Product product = Product.builder()
                .category(category)
                .name(input.getName())
                .description(input.getDescription())
                .price(input.getPrice())
                .stock(input.getStock())
                .build();

        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponseDto update(Long id, ProductServiceInputDto input) {
        validatePrice(input.getPrice());
        validateStock(input.getStock());

        Product product = findProduct(id);

        if (!product.getName().equals(input.getName())) {
            validateDuplicateName(input.getName());
        }

        Category category = findCategory(input.getCategoryId());
        product.updateDetails(
                category,
                input.getName(),
                input.getDescription(),
                input.getPrice(),
                input.getStock()
        );

        return productMapper.toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> search(ProductSearchCondition condition) {
        List<Product> products = productQueryRepository.findByCondition(condition);
        return productMapper.toResponseList(products);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> search(ProductSearchCondition condition,
                                           Pageable pageable) {
        Page<Product> products = productQueryRepository.findPageByCondition(condition, pageable);
        return products.map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponseDto> getCategorySummaries(Long categoryId) {
        return productMapper.toSummaryResponseList(productQueryRepository.findSummariesByCategoryId(categoryId));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));
    }

    private void validateDuplicateName(String name) {
        if (productRepository.existsByName(name)) {
            throw new ServiceException(ServiceExceptionCode.ALREADY_EXISTS_PRODUCT);
        }
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_PRODUCT_STOCK);
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(ServiceExceptionCode.INVALID_PRODUCT_PRICE);
        }
    }
}
