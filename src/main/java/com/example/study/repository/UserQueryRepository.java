package com.example.study.repository;

import com.example.study.entity.User;
import com.example.study.entity.QPurchase;
import com.example.study.entity.QUser;
import com.example.study.repository.projection.QUserPurchaseSummaryDto;
import com.example.study.repository.projection.QUserRecentActivityDto;
import com.example.study.repository.projection.UserPurchaseSummaryDto;
import com.example.study.repository.projection.UserRecentActivityDto;
import com.example.study.service.dto.UserSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자(User) 관련 QueryDSL 예제 모음.
 */
@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QUser user = QUser.user;
    private static final QPurchase purchase = QPurchase.purchase;

    /**
     * [기본 예제] 사용자 검색 조건 적용.
     */
    public List<User> findByCondition(UserSearchCondition condition) {
        UserSearchCondition safeCondition = condition != null ? condition : UserSearchCondition.builder().build();
        return queryFactory
                .selectFrom(user)
                .where(
                        nameContains(safeCondition.getNameKeyword()),
                        emailContains(safeCondition.getEmailKeyword()),
                        createdAfter(safeCondition.getCreatedAfter()),
                        createdBefore(safeCondition.getCreatedBefore()),
                        purchaseCountGoe(safeCondition.getMinPurchaseCount())
                )
                .orderBy(user.createdAt.desc())
                .fetch();
    }

    /**
     * [페이징 예제] 사용자 검색 + 페이징.
     */
    public Page<User> findByCondition(UserSearchCondition condition, Pageable pageable) {
        UserSearchCondition safeCondition = condition != null ? condition : UserSearchCondition.builder().build();
        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        nameContains(safeCondition.getNameKeyword()),
                        emailContains(safeCondition.getEmailKeyword()),
                        createdAfter(safeCondition.getCreatedAfter()),
                        createdBefore(safeCondition.getCreatedBefore()),
                        purchaseCountGoe(safeCondition.getMinPurchaseCount())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        nameContains(safeCondition.getNameKeyword()),
                        emailContains(safeCondition.getEmailKeyword()),
                        createdAfter(safeCondition.getCreatedAfter()),
                        createdBefore(safeCondition.getCreatedBefore()),
                        purchaseCountGoe(safeCondition.getMinPurchaseCount())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /**
     * [집계 예제] 사용자별 주문 요약.
     */
    public List<UserPurchaseSummaryDto> summarizePurchases() {
        return queryFactory
                .select(new QUserPurchaseSummaryDto(
                        user.id,
                        user.name,
                        user.email,
                        purchase.id.count(),
                        purchase.totalPrice.sum().coalesce(BigDecimal.ZERO),
                        purchase.purchasedAt.max()
                ))
                .from(user)
                .leftJoin(purchase).on(purchase.user.eq(user))
                .groupBy(user.id, user.name, user.email)
                .orderBy(purchase.totalPrice.sum().coalesce(BigDecimal.ZERO).desc())
                .fetch();
    }

    /**
     * [응용 예제] 최근 주문이 있는 활성 사용자 조회.
     */
    public List<UserRecentActivityDto> findActiveUsers(LocalDateTime since, int limit) {
        BooleanExpression datePredicate = since != null ? purchase.purchasedAt.goe(since) : null;

        return queryFactory
                .select(new QUserRecentActivityDto(
                        user.id,
                        user.name,
                        purchase.id.countDistinct(),
                        purchase.purchasedAt.max()
                ))
                .from(user)
                .join(purchase).on(purchase.user.eq(user))
                .where(datePredicate)
                .groupBy(user.id, user.name)
                .orderBy(purchase.purchasedAt.max().desc())
                .limit(limit)
                .fetch();
    }

    // ====================================
    // BooleanExpression builder methods
    // ====================================

    private BooleanExpression nameContains(String keyword) {
        return StringUtils.hasText(keyword) ? user.name.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression emailContains(String keyword) {
        return StringUtils.hasText(keyword) ? user.email.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression createdAfter(LocalDateTime createdAfter) {
        return createdAfter != null ? user.createdAt.goe(createdAfter) : null;
    }

    private BooleanExpression createdBefore(LocalDateTime createdBefore) {
        return createdBefore != null ? user.createdAt.loe(createdBefore) : null;
    }

    private BooleanExpression purchaseCountGoe(Integer minPurchaseCount) {
        if (minPurchaseCount == null) {
            return null;
        }
        return JPAExpressions
                .select(purchase.id.count())
                .from(purchase)
                .where(purchase.user.eq(user))
                .goe(minPurchaseCount.longValue());
    }
}
