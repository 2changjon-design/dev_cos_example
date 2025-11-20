package com.example.study.repository;

import com.example.study.entity.Product;
import com.example.study.repository.projection.ProductSalesSummaryDto;
import com.example.study.repository.projection.ProductSummaryDto;
import com.example.study.repository.projection.QProductSalesSummaryDto;
import com.example.study.repository.projection.QProductSummaryDto;
import com.example.study.service.dto.ProductSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.example.study.entity.QCategory.category;
import static com.example.study.entity.QProduct.product;
import static com.example.study.entity.QPurchase.purchase;

/**
 * 상품 관련 QueryDSL 예제 모음.
 * <p>
 * 기본 CRUD Repository(ProductRepository)는 그대로 두고,
 * 복잡한 조회 시나리오를 이 클래스로 분리하여 학생들이 참고할 수 있도록 구성했습니다.
 */
@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * [기본 예제] 여러 조건을 동적으로 조합하는 방법.
     * where 절에 null 이 들어가면 QueryDSL 이 자동으로 무시해주므로, 간단히 메서드로 분리했습니다.
     */
    public List<Product> findByCondition(ProductSearchCondition condition) {
        ProductSearchCondition safeCondition = condition != null ? condition : ProductSearchCondition.builder().build();
        return queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin() // 카테고리까지 함께 로딩
                .where(
                        nameContains(safeCondition.getName()),
                        priceGoe(safeCondition.getMinPrice()),
                        priceLoe(safeCondition.getMaxPrice()),
                        categoryEquals(safeCondition.getCategoryId()),
                        stockGoe(safeCondition.getMinStock()),
                        stockLoe(safeCondition.getMaxStock())
                )
                .orderBy(product.createdAt.desc())
                .fetch();
    }

    /**
     * [페이징 예제] QueryDSL 로 페이지네이션 구현하기.
     * fetchResults 가 deprecated 되었으므로, content 조회와 count 조회를 분리해서 작성합니다.
     */
    public Page<Product> findPageByCondition(ProductSearchCondition condition, Pageable pageable) {
        ProductSearchCondition safeCondition = condition != null ? condition : ProductSearchCondition.builder().build();
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin()
                .where(
                        nameContains(safeCondition.getName()),
                        priceGoe(safeCondition.getMinPrice()),
                        priceLoe(safeCondition.getMaxPrice()),
                        categoryEquals(safeCondition.getCategoryId()),
                        stockGoe(safeCondition.getMinStock()),
                        stockLoe(safeCondition.getMaxStock())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        nameContains(safeCondition.getName()),
                        priceGoe(safeCondition.getMinPrice()),
                        priceLoe(safeCondition.getMaxPrice()),
                        categoryEquals(safeCondition.getCategoryId()),
                        stockGoe(safeCondition.getMinStock()),
                        stockLoe(safeCondition.getMaxStock())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /**
     * [DTO 프로젝션 예제] 카테고리별 상품 요약.
     * QueryDSL 의 @QueryProjection 을 이용하면 select 절을 타입 안정하게 작성할 수 있습니다.
     */
    public List<ProductSummaryDto> findSummariesByCategoryId(Long categoryId) {
        return queryFactory
                .select(new QProductSummaryDto(
                        product.id,
                        product.name,
                        category.name,
                        product.price,
                        product.stock,
                        product.createdAt
                ))
                .from(product)
                .join(product.category, category)
                .where(categoryEquals(categoryId))
                .orderBy(product.createdAt.desc())
                .fetch();
    }

    /**
     * [기초 예제] 가장 비싼 상품 N개 조회.
     */
    public List<Product> findTopExpensiveProducts(int limit) {
        return queryFactory
                .selectFrom(product)
                .orderBy(product.price.desc())
                .limit(limit)
                .fetch();
    }

    /**
     * [응용 예제] 재고가 특정 기준 이하인 상품 조회.
     */
    public List<Product> findProductsNeedingRestock(int thresholdStock) {
        return queryFactory
                .selectFrom(product)
                .where(product.stock.loe(thresholdStock))
                .orderBy(product.stock.asc())
                .fetch();
    }

    /**
     * [집계 예제] 상품별 판매 통계.
     * Purchase 테이블과 조인하여 판매량/매출을 집계하는 예시입니다.
     */
    public List<ProductSalesSummaryDto> findProductSalesSummary() {
        return queryFactory
                .select(new QProductSalesSummaryDto(
                        product.id,
                        product.name,
                        purchase.quantity.sum().coalesce(0).castToNum(Long.class),
                        product.price.multiply(purchase.quantity.sum()
                                .coalesce(0)
                                .castToNum(BigDecimal.class))
                ))
                .from(product)
                .join(purchase).on(purchase.product.eq(product))
                .groupBy(product.id, product.name, product.price)
                .orderBy(purchase.quantity.sum().desc())
                .fetch();
    }

    // ==========================
    // 조건 메서드 (BooleanExpression)
    // ==========================

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? product.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression priceGoe(BigDecimal minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(BigDecimal maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }

    private BooleanExpression categoryEquals(Long categoryId) {
        return categoryId != null ? product.category.id.eq(categoryId) : null;
    }

    private BooleanExpression stockGoe(Integer minStock) {
        return minStock != null ? product.stock.goe(minStock) : null;
    }

    private BooleanExpression stockLoe(Integer maxStock) {
        return maxStock != null ? product.stock.loe(maxStock) : null;
    }
}
