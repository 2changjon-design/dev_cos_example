package com.example.study.entity;

import com.example.study.RefundStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table
@Entity
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    Purchase purchase;

    @Column(nullable = false, length = 255)
    String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    RefundStatus status;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Builder
    public Refund(Purchase purchase, String reason, RefundStatus status) {
        this.purchase = purchase;
        this.reason = reason;
        this.status = status;
    }
}
