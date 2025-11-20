package com.example.study.lecture.refactorspringsection2answer;

import com.example.study.lecture.refactorspringsection2.RefactorSpringSection2InventoryRequest;
import com.example.study.lecture.refactorspringsection2.RefactorSpringSection2InventoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lecture/refactor-spring-section2/answers/inventories")
public class RefactorSpringSection2InventoryControllerAfterAnswer {

    private final RefactorSpringSection2InventoryServiceAfterAnswer service;

    public RefactorSpringSection2InventoryControllerAfterAnswer(
            RefactorSpringSection2InventoryServiceAfterAnswer service
    ) {
        this.service = service;
    }

    @PatchMapping
    public ResponseEntity<RefactorSpringSection2InventoryResponse> adjust(
            @Valid @RequestBody RefactorSpringSection2InventoryRequest request
    ) {
        return ResponseEntity.ok(service.adjustInventory(request));
    }
}
