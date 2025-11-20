package com.example.study.repository.practice;

import com.example.study.repository.projection.CategoryProductDto;
import com.example.study.repository.projection.QCategoryProductDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.study.entity.QCategory.category;
import static com.example.study.entity.QProduct.product;

/**
 * 실습 예제 1: 카테고리와 상품을 조인하여 DTO로 조회하는 Repository.
 */
@Repository
@RequiredArgsConstructor
public class CategoryProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 실습: QueryDSL 조인 + @QueryProjection DTO 매핑.
     *
     * @param categoryName 조회할 카테고리 이름
     * @return CategoryProductDto 목록
     */
    public List<CategoryProductDto> findCategoryProducts(String categoryName) {
        return queryFactory
                .select(new QCategoryProductDto(
                        category.name,
                        product.name,
                        product.price,
                        product.stock
                ))
                .from(product)
                .join(product.category, category)
                .where(categoryNameEquals(categoryName))
                .orderBy(product.name.asc())
                .fetch();
    }

    private BooleanExpression categoryNameEquals(String categoryName) {
        return categoryName != null ? category.name.eq(categoryName) : null;
    }
}
