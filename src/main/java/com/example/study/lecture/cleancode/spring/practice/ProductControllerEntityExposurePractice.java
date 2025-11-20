package com.example.study.lecture.cleancode.spring.practice;

import com.example.study.entity.Product;
import com.example.study.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spring 연습용 예제 2: 엔티티를 그대로 노출하는 API.
 * - DTO/Mapper 부재
 * - 입력값 검증 없음
 * - Product 엔티티 필드가 외부 계약으로 고정되어 변경 비용이 커짐
 *
 * TODO(SPRING-LAB):
 *  1) 요청/응답 DTO를 만들고, 엔티티-DTO 변환을 전담하는 Mapper/Assembler를 도입하세요.
 *  2) 컨트롤러에서는 가벼운 orchestration만 수행하고, 서비스에 책임을 위임하세요.
 *  3) 검증/에러 응답 표준화를 적용하세요.
 */
@RestController
@RequestMapping("/spring/practice/products")
public class ProductControllerEntityExposurePractice {

    private final ProductRepository productRepository;

    public ProductControllerEntityExposurePractice(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product body) {
        if (body.getName() == null || body.getPrice() == null) {
            throw new IllegalArgumentException("name/price required");
        }
        if (body.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            body.updateDetails(body.getCategory(), body.getName(), body.getDescription(), BigDecimal.ONE, body.getStock());
        }
        return productRepository.save(body);
    }

    @PatchMapping("/{id}/price")
    public Product changePrice(@PathVariable Long id, @RequestBody Product request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));
        product.updateDetails(
                request.getCategory() != null ? request.getCategory() : product.getCategory(),
                request.getName() != null ? request.getName() : product.getName(),
                request.getDescription() != null ? request.getDescription() : product.getDescription(),
                request.getPrice(),
                request.getStock() != null ? request.getStock() : product.getStock()
        );
        return productRepository.save(product);
    }
}
