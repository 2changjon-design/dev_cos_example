package com.example.study.repository.practice;

import com.example.study.entity.Product;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.example.study.entity.QProduct.product;

/**
 * 실습 예제 3: BooleanExpression 으로 동적 검색 조건을 구현하는 Repository.
 */
@Repository
@RequiredArgsConstructor
public class ProductSearchPracticeRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 실습: 이름/가격 조건이 있는 경우에만 where 절에 적용한다.
     */
    public List<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        return queryFactory
                .selectFrom(product)
                .where(
                        nameContains(name),
                        priceGoe(minPrice),
                        priceLoe(maxPrice)
                )
                .orderBy(product.createdAt.desc())
                .fetch();
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? product.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression priceGoe(BigDecimal minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(BigDecimal maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }
}
