package com.example.study.controller;

import com.example.study.controller.dto.LessonEligibilityRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LessonChecklistControllerAAATest {

    // MockMvc는 HTTP 요청 시뮬레이션에 사용된다.
    @Autowired
    MockMvc mockMvc;

    // JSON 직렬화를 위해 ObjectMapper를 주입받는다.
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[AAA] 적격성 체크 성공")
    void checkEligibility_shouldReturnSuccessResponse() throws Exception {
        // Arrange: 유효한 요청 DTO를 만든다.
        // DTO를 생성한다.
        LessonEligibilityRequest request = new LessonEligibilityRequest(3, 4);
        // DTO를 JSON으로 변환한다.
        String body = objectMapper.writeValueAsString(request);

        // Act & Assert: POST 호출 및 응답 검증을 수행한다.
        mockMvc.perform(post("/lesson/check-eligibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.message").value("축하합니다! 실습을 진행할 준비가 되었습니다."));
    }

    @Test
    @DisplayName("[AAA] 적격성 체크 실패 - 검증 오류")
    void checkEligibility_shouldValidateInput() throws Exception {
        // Arrange: 음수 경력을 포함한 JSON 문자열을 준비한다.
        // 잘못된 JSON 문자열을 정의한다.
        String invalidBody = """
                {
                  \"experienceYears\": -1,
                  \"completedProjects\": 1
                }
                """;

        // Act & Assert: Bean Validation으로 400 상태를 기대한다.
        mockMvc.perform(post("/lesson/check-eligibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[AAA] 실습시간 계산 성공")
    void practiceHours_shouldReturnHours() throws Exception {
        // Arrange: 4주 질의를 위한 쿼리 파라미터 값을 준비한다.
        // 주차 수를 정수로 저장한다.
        int weeks = 4;

        // Act & Assert: GET 요청으로 응답 구조를 확인한다.
        mockMvc.perform(get("/lesson/practice-hours")
                        .param("weeks", String.valueOf(weeks)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weeks").value(weeks))
                .andExpect(jsonPath("$.hours").value(20));
    }
}
