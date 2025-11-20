package com.example.study.repository;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
import com.example.study.entity.QProduct;
import com.example.study.entity.QUser;
import com.example.study.repository.projection.PurchaseDailyReportDto;
import com.example.study.repository.projection.PurchaseDetailDto;
import com.example.study.repository.projection.PurchaseStatusCountDto;
import com.example.study.repository.projection.QPurchaseDailyReportDto;
import com.example.study.repository.projection.QPurchaseDetailDto;
import com.example.study.repository.projection.QPurchaseStatusCountDto;
import com.example.study.service.dto.PurchaseSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.study.entity.QPurchase.purchase;

/**
 * 주문(Purchase) 관련 QueryDSL 예제 모음.
 */
@Repository
@RequiredArgsConstructor
public class PurchaseQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QUser user = QUser.user;
    private static final QProduct product = QProduct.product;

    /**
     * [Fetch Join 예제] 주문과 연관된 사용자, 상품을 한 번에 로딩.
     */
    public List<Purchase> findAllWithUserAndProduct() {
        return queryFactory
                .selectFrom(purchase)
                .join(purchase.user, user).fetchJoin()
                .join(purchase.product, product).fetchJoin()
                .orderBy(purchase.purchasedAt.desc())
                .fetch();
    }

    /**
     * [DTO 프로젝션 + 동적 쿼리] 주문 상세 검색.
     * QueryDSL 의 BooleanExpression 메서드를 활용해서 다양한 조건을 조합합니다.
     */
    public List<PurchaseDetailDto> searchPurchaseDetails(PurchaseSearchCondition condition) {
        PurchaseSearchCondition safeCondition = condition != null ? condition : PurchaseSearchCondition.builder().build();
        return queryFactory
                .select(new QPurchaseDetailDto(
                        purchase.id,
                        user.id,
                        user.name,
                        product.id,
                        product.name,
                        purchase.quantity,
                        purchase.unitPrice,
                        purchase.totalPrice,
                        purchase.status,
                        purchase.purchasedAt
                ))
                .from(purchase)
                .join(purchase.user, user)
                .join(purchase.product, product)
                .where(buildCommonConditions(safeCondition))
                .orderBy(purchase.purchasedAt.desc())
                .fetch();
    }

    /**
     * [페이징 + DTO 프로젝션] 주문 상세 페이징 조회.
     */
    public Page<PurchaseDetailDto> searchPurchaseDetails(PurchaseSearchCondition condition, Pageable pageable) {
        PurchaseSearchCondition safeCondition = condition != null ? condition : PurchaseSearchCondition.builder().build();
        List<PurchaseDetailDto> content = queryFactory
                .select(new QPurchaseDetailDto(
                        purchase.id,
                        user.id,
                        user.name,
                        product.id,
                        product.name,
                        purchase.quantity,
                        purchase.unitPrice,
                        purchase.totalPrice,
                        purchase.status,
                        purchase.purchasedAt
                ))
                .from(purchase)
                .join(purchase.user, user)
                .join(purchase.product, product)
                .where(buildCommonConditions(safeCondition))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(purchase.purchasedAt.desc())
                .fetch();

        Long total = queryFactory
                .select(purchase.count())
                .from(purchase)
                .where(buildCommonConditions(safeCondition))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /**
     * [집계 예제] 상태별 주문 수 카운트.
     */
    public List<PurchaseStatusCountDto> countByStatus(PurchaseSearchCondition condition) {
        PurchaseSearchCondition safeCondition = condition != null ? condition : PurchaseSearchCondition.builder().build();
        return queryFactory
                .select(new QPurchaseStatusCountDto(
                        purchase.status,
                        purchase.count()
                ))
                .from(purchase)
                .where(buildCommonConditions(safeCondition))
                .groupBy(purchase.status)
                .orderBy(purchase.status.asc())
                .fetch();
    }

    /**
     * [응용 예제] 일자별 주문/매출 리포트.
     */
    public List<PurchaseDailyReportDto> summarizeDaily(LocalDate startDate, LocalDate endDate) {
        BooleanExpression datePredicate = betweenDate(startDate, endDate);
        var orderDateExpression = Expressions.dateTemplate(LocalDate.class, "DATE({0})", purchase.purchasedAt);

        return queryFactory
                .select(new QPurchaseDailyReportDto(
                        orderDateExpression,
                        purchase.count(),
                        purchase.totalPrice.sum().coalesce(BigDecimal.ZERO)
                ))
                .from(purchase)
                .where(datePredicate)
                .groupBy(orderDateExpression)
                .orderBy(orderDateExpression.asc())
                .fetch();
    }

    // ====================================
    // BooleanExpression builder methods
    // ====================================

    private BooleanExpression[] buildCommonConditions(PurchaseSearchCondition condition) {
        if (condition == null) {
            return new BooleanExpression[]{};
        }
        return new BooleanExpression[]{
                userIdEq(condition.getUserId()),
                productIdEq(condition.getProductId()),
                statusEq(condition.getStatus()),
                purchasedAfter(condition.getPurchasedAfter()),
                purchasedBefore(condition.getPurchasedBefore()),
                quantityGoe(condition.getMinQuantity()),
                quantityLoe(condition.getMaxQuantity())
        };
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? purchase.user.id.eq(userId) : null;
    }

    private BooleanExpression productIdEq(Long productId) {
        return productId != null ? purchase.product.id.eq(productId) : null;
    }

    private BooleanExpression statusEq(PurchaseStatus status) {
        return status != null ? purchase.status.eq(status) : null;
    }

    private BooleanExpression purchasedAfter(LocalDateTime dateTime) {
        return dateTime != null ? purchase.purchasedAt.goe(dateTime) : null;
    }

    private BooleanExpression purchasedBefore(LocalDateTime dateTime) {
        return dateTime != null ? purchase.purchasedAt.loe(dateTime) : null;
    }

    private BooleanExpression quantityGoe(Integer minQuantity) {
        return minQuantity != null ? purchase.quantity.goe(minQuantity) : null;
    }

    private BooleanExpression quantityLoe(Integer maxQuantity) {
        return maxQuantity != null ? purchase.quantity.loe(maxQuantity) : null;
    }

    private BooleanExpression betweenDate(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return null;
        }
        BooleanExpression predicate = null;
        if (start != null) {
            predicate = purchase.purchasedAt.goe(start.atStartOfDay());
        }
        if (end != null) {
            BooleanExpression endCondition = purchase.purchasedAt.loe(end.atTime(23, 59, 59));
            predicate = predicate != null ? predicate.and(endCondition) : endCondition;
        }
        return predicate;
    }
}
