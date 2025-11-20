package com.example.study.lecture;

import com.example.study.PurchaseStatus;
import com.example.study.entity.Product;
import com.example.study.entity.Purchase;
import com.example.study.entity.User;
import com.example.study.repository.ProductRepository;
import com.example.study.repository.PurchaseRepository;
import com.example.study.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class JpaLectureExamples {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserJpaRepository userJpaRepository;

    public JpaLectureExamples(
            ProductRepository productRepository,
            PurchaseRepository purchaseRepository,
            UserJpaRepository userJpaRepository
    ) {
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.userJpaRepository = userJpaRepository;
    }

    // [영속성 컨텍스트] 엔티티 라이프사이클 네 단계 확인하기
    @Transactional
    public void showEntityLifecycle() {
        User transientUser = User.builder()
                .name("Lifecycle User")
                .email("lifecycle@example.com")
                .passwordHash("secure-password")
                .build();

        entityManager.persist(transientUser);

        entityManager.flush();

        entityManager.detach(transientUser);

        User managedUser = entityManager.find(User.class, transientUser.getId());
        if (managedUser != null) {
            entityManager.remove(managedUser);
        }
    }

    // [영속성 컨텍스트] 1차 캐시와 동일성 보장 예시
    @Transactional(readOnly = true)
    public boolean firstLevelCacheIdentity(Long userId) {
        User first = entityManager.find(User.class, userId);
        User second = entityManager.find(User.class, userId);
        return first != null && first == second;
    }

    // [영속성 컨텍스트] 쓰기 지연 SQL 저장소 예시
    @Transactional
    public void transactionalWriteBehindExample() {
        User user = User.builder()
                .name("WriteBehind User")
                .email("write-behind@example.com")
                .passwordHash("pw1234")
                .build();
        entityManager.persist(user);

        Product product = Product.builder()
                .name("Buffered Laptop")
                .description("커밋 시점에 INSERT 됩니다.")
                .price(BigDecimal.valueOf(1299000))
                .stock(10)
                .build();
        entityManager.persist(product);
    }

    // [트랜잭션] ACID 보장을 위한 주문 처리 흐름 예시
    @Transactional
    public void placeOrderWithRollback(OrderExampleRequest request) {
        User user = userJpaRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("주문자 정보를 찾을 수 없습니다."));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품 정보를 찾을 수 없습니다."));

        if (product.getStock() < request.quantity()) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        Purchase purchase = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(request.quantity())
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.quantity())))
                .status(PurchaseStatus.PENDING)
                .build();
        purchaseRepository.save(purchase);

        product.decreaseStock(request.quantity());

        if (product.getStock() < 0) {
            throw new IllegalStateException("재고가 음수가 되었습니다.");
        }
    }

    // [Dirty Checking] 엔티티 필드만 바꾸고 커밋하기
    @Transactional
    public void decreaseStockWithDirtyChecking(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 정보를 찾을 수 없습니다."));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        product.decreaseStock(quantity);
    }

    // [Flush] JPQL 실행 직전 자동 동기화 예시
    @Transactional
    public Product findImmediatelyAfterPersist(String name) {
        Product newProduct = Product.builder()
                .name(name + "-" + System.nanoTime())
                .description("JPQL 실행 전에 flush 되는지 확인합니다.")
                .price(BigDecimal.valueOf(59000))
                .stock(5)
                .build();
        entityManager.persist(newProduct);

        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.name = :name",
                Product.class
        );
        query.setParameter("name", newProduct.getName());
        return query.getSingleResult();
    }

    // [Flush] 명시적으로 flush 호출하고 외부 로직 실행하기
    @Transactional
    public long persistAndQueryWithFlush(Product newProduct) {
        Objects.requireNonNull(newProduct, "newProduct must not be null");
        entityManager.persist(newProduct);

        entityManager.flush();

        return productRepository.findByName(newProduct.getName()).size();
    }

    public record OrderExampleRequest(Long userId, Long productId, int quantity) {
    }
}
