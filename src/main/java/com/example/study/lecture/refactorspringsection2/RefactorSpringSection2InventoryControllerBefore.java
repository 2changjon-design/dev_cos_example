package com.example.study.lecture.refactorspringsection2;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Section2 Controller BEFORE
 *
 * 리팩토링 힌트:
 * - Controller는 Service 호출만 담당하도록 유지하되, After Answer처럼 에러 응답/경로 체계를 정리해보자.
 * - 예외 처리를 일관된 방식으로 분리해 테스트와 유지보수가 쉽도록 설계하자.
 */
@RestController
@RequestMapping("/lecture/refactor-spring-section2/inventories")
public class RefactorSpringSection2InventoryControllerBefore {

    private final RefactorSpringSection2InventoryServiceBefore inventoryService;

    public RefactorSpringSection2InventoryControllerBefore(RefactorSpringSection2InventoryServiceBefore inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PatchMapping
    public ResponseEntity<RefactorSpringSection2InventoryResponse> adjust(
            @Valid @RequestBody RefactorSpringSection2InventoryRequest request
    ) {
        return ResponseEntity.ok(inventoryService.adjustInventory(request));
    }
}
