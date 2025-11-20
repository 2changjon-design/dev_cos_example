package com.example.study.controller.mock;

// 테스트에서 사용할 Hamcrest 및 MockMvc 유틸리티를 정적 임포트한다.
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// 통합 테스트 컨텍스트 설정
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MockApiControllerGWTTest {

    // MockMvc로 HTTP 요청을 시뮬레이션한다.
    @Autowired
    MockMvc mockMvc;

    // GWT 테스트에서도 JSON 직렬화를 위해 ObjectMapper를 사용한다.
    @Autowired
    ObjectMapper objectMapper;

    // Given-When-Then 패턴으로 상품 목록 조회를 표현한다.
    @Test
    @DisplayName("[GWT] 상품 조회 API")
    void givenSeedData_whenGetProducts_thenReturnsList() throws Exception {
        // Given: 특별한 준비 없이 시드 데이터가 컨텍스트에 존재한다.
        // When & Then: GET 요청을 보내고 응답 구조를 검증한다.
        mockMvc.perform(get("/mock/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(3)));
    }

    // 회원가입 성공 시나리오를 GWT 스타일로 작성한다.
    @Test
    @DisplayName("[GWT] 회원가입 성공")
    void givenValidPayload_whenPostUsers_thenReturns201() throws Exception {
        // Given: 유효한 사용자 등록 요청을 만든다.
        MockApiController.MockUserRequest request =
                new MockApiController.MockUserRequest("gwt-user", "gwt@example.com", "pass1234");

        // When: POST /mock/users 호출
        // Then: 201 상태와 username 필드를 검증
        mockMvc.perform(post("/mock/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("gwt-user"));
    }

    // 잘못된 이메일 형식을 보냈을 때를 검증한다.
    @Test
    @DisplayName("[GWT] 회원가입 실패 - 이메일 형식")
    void givenInvalidEmail_whenPostUsers_thenReturns400() throws Exception {
        // Given: 이메일 형식이 틀린 JSON Body
        String invalidRequest = """
                {
                  \"username\": \"mock-user\",
                  \"email\": \"invalid-email\",
                  \"password\": \"pass1234\"
                }
                """;

        // When & Then: 400 상태를 기대한다.
        mockMvc.perform(post("/mock/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    // 존재하지 않는 상품을 삭제할 때 400이 내려오는지 확인한다.
    @Test
    @DisplayName("[GWT] 없는 상품 삭제")
    void givenMissingProduct_whenDelete_thenReturns400() throws Exception {
        mockMvc.perform(delete("/mock/products/{productId}", 999))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.errorCode").value("NOT_FOUND_PRODUCT"));
    }

    // 정상 삭제 시 deleted=true가 응답되는지 확인한다.
    @Test
    @DisplayName("[GWT] 상품 삭제 성공")
    void givenExistingProduct_whenDelete_thenDeletesOnce() throws Exception {
        mockMvc.perform(delete("/mock/products/{productId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true));
    }

    // 이미 삭제된 상품을 다시 삭제하면 409가 응답되는지 검증한다.
    @Test
    @DisplayName("[GWT] 이미 삭제된 상품")
    void givenDeletedProduct_whenDeleteAgain_thenReturns409() throws Exception {
        mockMvc.perform(delete("/mock/products/{productId}", 1))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/mock/products/{productId}", 1))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.errorCode").value("ALREADY_DELETED"));
    }
}
