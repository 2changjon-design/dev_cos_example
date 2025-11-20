package com.example.study.repository;

import com.example.study.RefundStatus;
import com.example.study.entity.QProduct;
import com.example.study.entity.QPurchase;
import com.example.study.entity.QRefund;
import com.example.study.entity.QUser;
import com.example.study.entity.Refund;
import com.example.study.repository.projection.QRefundDetailDto;
import com.example.study.repository.projection.QRefundStatusCountDto;
import com.example.study.repository.projection.RefundDetailDto;
import com.example.study.repository.projection.RefundStatusCountDto;
import com.example.study.service.dto.RefundSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 환불(Refund) 관련 QueryDSL 예제 모음.
 */
@Repository
@RequiredArgsConstructor
public class RefundQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QRefund refund = QRefund.refund;
    private static final QPurchase purchase = QPurchase.purchase;
    private static final QUser user = QUser.user;
    private static final QProduct product = QProduct.product;

    /**
     * [Fetch Join 예제] 환불과 관련된 구매, 사용자, 상품 정보를 한 번에 불러오기.
     */
    public List<Refund> findAllWithRelations() {
        return queryFactory
                .selectFrom(refund)
                .join(refund.purchase, purchase).fetchJoin()
                .join(purchase.user, user).fetchJoin()
                .join(purchase.product, product).fetchJoin()
                .orderBy(refund.createdAt.desc())
                .fetch();
    }

    /**
     * [DTO 프로젝션 + 동적 쿼리] 환불 상세 목록 조회.
     */
    public List<RefundDetailDto> searchRefundDetails(RefundSearchCondition condition) {
        RefundSearchCondition safeCondition = condition != null ? condition : RefundSearchCondition.builder().build();
        return queryFactory
                .select(new QRefundDetailDto(
                        refund.id,
                        purchase.id,
                        user.id,
                        user.name,
                        product.id,
                        product.name,
                        refund.status,
                        refund.reason,
                        refund.createdAt
                ))
                .from(refund)
                .join(refund.purchase, purchase)
                .join(purchase.user, user)
                .join(purchase.product, product)
                .where(buildConditions(safeCondition))
                .orderBy(refund.createdAt.desc())
                .fetch();
    }

    /**
     * [집계 예제] 환불 상태별 건수 파악.
     */
    public List<RefundStatusCountDto> countByStatus(RefundSearchCondition condition) {
        RefundSearchCondition safeCondition = condition != null ? condition : RefundSearchCondition.builder().build();
        return queryFactory
                .select(new QRefundStatusCountDto(
                        refund.status,
                        refund.count()
                ))
                .from(refund)
                .where(buildConditions(safeCondition))
                .groupBy(refund.status)
                .orderBy(refund.status.asc())
                .fetch();
    }

    // ====================================
    // BooleanExpression builder methods
    // ====================================

    private BooleanExpression[] buildConditions(RefundSearchCondition condition) {
        if (condition == null) {
            return new BooleanExpression[]{};
        }
        return new BooleanExpression[]{
                purchaseIdEq(condition.getPurchaseId()),
                userIdEq(condition.getUserId()),
                statusEq(condition.getStatus()),
                createdAfter(condition.getCreatedAfter()),
                createdBefore(condition.getCreatedBefore())
        };
    }

    private BooleanExpression purchaseIdEq(Long purchaseId) {
        return purchaseId != null ? refund.purchase.id.eq(purchaseId) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? purchase.user.id.eq(userId) : null;
    }

    private BooleanExpression statusEq(RefundStatus status) {
        return status != null ? refund.status.eq(status) : null;
    }

    private BooleanExpression createdAfter(LocalDateTime createdAfter) {
        return createdAfter != null ? refund.createdAt.goe(createdAfter) : null;
    }

    private BooleanExpression createdBefore(LocalDateTime createdBefore) {
        return createdBefore != null ? refund.createdAt.loe(createdBefore) : null;
    }
}
