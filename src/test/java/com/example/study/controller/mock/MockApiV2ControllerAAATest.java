package com.example.study.controller.mock;

// MockMvc 요청/응답 검증에 필요한 정적 메서드를 임포트한다.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// Mock API v2 컨트롤러에 대한 AAA 패턴 테스트 클래스
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MockApiV2ControllerAAATest {

    // MockMvc를 주입받아 컨트롤러를 호출한다.
    @Autowired
    MockMvc mockMvc;

    // 시드 데이터 목록 조회가 200인지 확인한다.
    @Test
    @DisplayName("v2 상품 목록 API는 기본 시드 상품을 반환한다")
    void products_shouldReturnSeedList() throws Exception {
        mockMvc.perform(get("/mock/v2/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    // 존재하지 않는 상품 조회 시 404와 에러 코드가 내려오는지 확인한다.
    @Test
    @DisplayName("v2 상품 상세 API는 존재하지 않는 상품에 대해 404를 반환한다")
    void product_shouldReturnNotFound_whenMissing() throws Exception {
        mockMvc.perform(get("/mock/v2/products/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("NOT_FOUND_PRODUCT"));
    }

    // 상품 생성 성공 케이스를 검증한다.
    @Test
    @DisplayName("v2 상품 생성 API는 유효한 요청일 경우 201을 반환한다")
    void createProduct_shouldReturnCreated() throws Exception {
        String request = """
                {
                  \"name\": \"새로운 상품\",
                  \"price\": 12000,
                  \"stock\": 7
                }
                """;

        mockMvc.perform(post("/mock/v2/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.name").value("새로운 상품"))
                .andExpect(jsonPath("$.data.stock").value(7));
    }

    // 검증 실패 시 400을 반환하는지 검증한다.
    @Test
    @DisplayName("v2 상품 생성 API는 잘못된 가격일 경우 400을 반환한다")
    void createProduct_shouldReturnBadRequest_whenPriceInvalid() throws Exception {
        String request = """
                {
                  \"name\": \"invalid\",
                  \"price\": 0,
                  \"stock\": 1
                }
                """;

        mockMvc.perform(post("/mock/v2/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    // 재고 증가 요청이 성공적으로 처리되는지 확인한다.
    @Test
    @DisplayName("v2 재고 조정 API는 수량을 증가시킬 수 있다")
    void adjustStock_shouldIncreaseStock() throws Exception {
        String request = """
                {
                  \"quantityDelta\": 5
                }
                """;

        mockMvc.perform(patch("/mock/v2/products/{id}/stock", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(20));
    }

    // 재고를 음수로 만들면 409와 에러 코드가 나오는지 확인한다.
    @Test
    @DisplayName("v2 재고 조정 API는 재고가 음수가 되면 409를 반환한다")
    void adjustStock_shouldFail_whenResultNegative() throws Exception {
        String request = """
                {
                  \"quantityDelta\": -100
                }
                """;

        mockMvc.perform(patch("/mock/v2/products/{id}/stock", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("INSUFFICIENT_STOCK"));
    }

    // 삭제 후 재삭제 시 409가 반환되는지 검증한다.
    @Test
    @DisplayName("v2 상품 삭제 API는 이미 삭제된 상품에 대해 409를 반환한다")
    void deleteProduct_shouldHandleAlreadyDeleted() throws Exception {
        mockMvc.perform(delete("/mock/v2/products/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true));

        mockMvc.perform(delete("/mock/v2/products/{id}", 1))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.errorCode").value("ALREADY_DELETED"));
    }
}
