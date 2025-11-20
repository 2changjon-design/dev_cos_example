package com.example.study.lecture.cleancode.spring.answer3.settlement;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Purchase;
import com.example.study.repository.PurchaseRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class PendingPurchaseFinder {

    private final PurchaseRepository purchaseRepository;

    public PendingPurchaseFinder(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<Purchase> findBatch(int size) {
        return purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getStatus() == PurchaseStatus.PENDING)
                .sorted(Comparator.comparing(Purchase::getPurchasedAt))
                .limit(size)
                .toList();
    }
}
