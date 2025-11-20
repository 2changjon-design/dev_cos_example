package com.example.study.controller;

// JSON 배열 길이를 검증하기 위해 Hamcrest 매처를 정적 임포트한다.

import com.example.study.controller.mock.MockApiController;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 통합 테스트 환경에서 MockMvc를 사용하기 위한 공통 설정을 적용한다.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MockApiControllerTest {

    // MockMvc를 주입받아 컨트롤러를 서버 띄우지 않고 호출할 수 있게 한다.
    @Autowired
    MockMvc mockMvc;

    // DTO ↔ JSON 변환을 위해 ObjectMapper를 준비한다.
    @Autowired
    ObjectMapper objectMapper;

    // AAA 패턴의 Arrange-Act-Assert 순서로 상품 목록 조회를 검증한다.
    @Test
    @DisplayName("상품 조회 API는 시드된 상품 목록을 반환한다")
    void getProducts_shouldReturnSeedProducts() throws Exception {
        // Arrange: 별도의 준비가 필요 없는 엔드포인트이므로 생략한다.
        // Act & Assert: GET 요청을 수행하고 상태/Body를 검증한다.
        mockMvc.perform(get("/mock/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(3)));
    }

    // 회원가입 성공 케이스를 AAA 패턴으로 설명한다.
    @Test
    @DisplayName("회원가입 API는 올바른 요청일 때 201을 반환한다")
    void registerUser_shouldReturnCreated_whenRequestValid() throws Exception {
        // Arrange: 성공적으로 등록될 DTO를 만든다.
        MockApiController.MockUserRequest request =
                new MockApiController.MockUserRequest("mock-user", "mock@example.com", "pass1234");

        // Act & Assert: POST 요청을 보내고 상태/필드를 검증한다.
        mockMvc.perform(post("/mock/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.username").value("mock-user"))
                .andExpect(jsonPath("$.data.email").value("mock@example.com"));
    }

    // 이메일 형식 오류 시 400을 반환하는지 확인한다.
    @Test
    @DisplayName("회원가입 API는 이메일 형식이 잘못되면 400을 반환한다")
    void registerUser_shouldReturnBadRequest_whenEmailInvalid() throws Exception {
        // Arrange: 유효하지 않은 JSON 문자열을 준비한다.
        String invalidRequest = """
                {
                  \"username\": \"mock-user\",
                  \"email\": \"invalid-email\",
                  \"password\": \"pass1234\"
                }
                """;

        // Act & Assert: 잘못된 Body를 전송하면 400이 내려오는지 확인한다.
        mockMvc.perform(post("/mock/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    // 존재하지 않는 상품 삭제 시 400 + 에러 코드를 검증한다.
    @Test
    @DisplayName("상품 삭제 API는 존재하지 않는 상품일 경우 400을 반환한다")
    void deleteProduct_shouldReturnBadRequest_whenProductMissing() throws Exception {
        mockMvc.perform(delete("/mock/products/{productId}", 999))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("NOT_FOUND_PRODUCT"));
    }

    // 정상 삭제 케이스를 살펴본다.
    @Test
    @DisplayName("상품 삭제 API는 정상 삭제 후 deleted=true를 반환한다")
    void deleteProduct_shouldMarkProductDeleted_whenValid() throws Exception {
        mockMvc.perform(delete("/mock/products/{productId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.deleted").value(true));
    }

    // 이미 삭제된 상품을 다시 삭제할 때의 동작을 검증한다.
    @Test
    @DisplayName("이미 삭제된 상품을 다시 삭제하면 409를 반환한다")
    void deleteProduct_shouldReturnConflict_whenAlreadyDeleted() throws Exception {
        // Arrange/Act: 첫 번째 삭제는 성공시킨다.
        mockMvc.perform(delete("/mock/products/{productId}", 1))
                .andExpect(status().isOk());

        // Assert: 두 번째 삭제는 409를 반환해야 한다.
        mockMvc.perform(delete("/mock/products/{productId}", 1))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("ALREADY_DELETED"));
    }
}
