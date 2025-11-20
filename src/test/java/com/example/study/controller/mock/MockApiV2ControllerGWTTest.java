package com.example.study.controller.mock;

// MockMvc 요청 빌더와 검증 메서드를 정적 임포트한다.
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

// v2 컨트롤러에 대한 GWT 스타일 테스트 클래스
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MockApiV2ControllerGWTTest {

    // MockMvc로 HTTP 요청을 시뮬레이션한다.
    @Autowired
    MockMvc mockMvc;

    // Given-When-Then으로 기본 목록 조회를 표현한다.
    @Test
    @DisplayName("[GWT] 기본 목록 조회")
    void givenSeed_whenGetProducts_thenReturnsList() throws Exception {
        mockMvc.perform(get("/mock/v2/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    // 존재하지 않는 상품 조회 시 404가 나오는지 확인한다.
    @Test
    @DisplayName("[GWT] 단일 상품 404")
    void givenMissingId_whenGetProduct_thenReturns404() throws Exception {
        mockMvc.perform(get("/mock/v2/products/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.errorCode").value("NOT_FOUND_PRODUCT"));
    }

    // 상품 생성 성공 케이스를 서술한다.
    @Test
    @DisplayName("[GWT] 상품 생성 성공")
    void givenValidBody_whenPostProduct_thenReturns201() throws Exception {
        String request = """
                {
                  \"name\": \"GWT 상품\",
                  \"price\": 9900,
                  \"stock\": 3
                }
                """;

        mockMvc.perform(post("/mock/v2/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("GWT 상품"));
    }

    // 검증 실패 케이스를 다룬다.
    @Test
    @DisplayName("[GWT] 상품 생성 실패")
    void givenInvalidBody_whenPostProduct_thenReturns400() throws Exception {
        String request = """
                {
                  \"name\": \"invalid\",
                  \"price\": -1,
                  \"stock\": 1
                }
                """;

        mockMvc.perform(post("/mock/v2/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    // 재고 증가 시나리오를 검증한다.
    @Test
    @DisplayName("[GWT] 재고 증가")
    void givenQuantityDelta_whenPatchStock_thenUpdates() throws Exception {
        String request = """
                {
                  \"quantityDelta\": 2
                }
                """;

        mockMvc.perform(patch("/mock/v2/products/{id}/stock", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(17));
    }

    // 재고 감소 실패 시 409를 검증한다.
    @Test
    @DisplayName("[GWT] 재고 감소 실패")
    void givenLargeNegative_whenPatchStock_thenReturns409() throws Exception {
        String request = """
                {
                  \"quantityDelta\": -100
                }
                """;

        mockMvc.perform(patch("/mock/v2/products/{id}/stock", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.errorCode").value("INSUFFICIENT_STOCK"));
    }

    // 삭제 후 중복 삭제 시 409 응답을 확인한다.
    @Test
    @DisplayName("[GWT] 삭제 후 중복 삭제")
    void givenDeletedProduct_whenDeleteAgain_then409() throws Exception {
        mockMvc.perform(delete("/mock/v2/products/{id}", 1))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/mock/v2/products/{id}", 1))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.errorCode").value("ALREADY_DELETED"));
    }
}
