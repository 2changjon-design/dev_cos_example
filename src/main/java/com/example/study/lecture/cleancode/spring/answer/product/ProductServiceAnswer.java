package com.example.study.lecture.cleancode.spring.answer.product;

import com.example.study.entity.Product;
import com.example.study.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceAnswer {

    private final ProductRepository productRepository;
    private final ProductMapperAnswer mapper;

    public ProductServiceAnswer(ProductRepository productRepository, ProductMapperAnswer mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        request.validate();
        Product product = mapper.toEntity(request);
        Product saved = productRepository.save(product);
        return mapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse changePrice(Long productId, UpdateProductPriceRequest request) {
        request.validate();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        product.updateDetails(
                product.getCategory(),
                product.getName(),
                product.getDescription(),
                request.newPrice(),
                product.getStock()
        );
        return mapper.toResponse(productRepository.save(product));
    }
}
