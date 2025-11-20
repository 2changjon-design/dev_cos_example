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
class LessonChecklistControllerGWTTest {

    // MockMvc 인스턴스를 주입받는다.
    @Autowired
    MockMvc mockMvc;

    // ObjectMapper는 Body 직렬화에 사용된다.
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[GWT] 적격성 체크 성공")
    void givenValidPayload_whenCheckEligibility_thenReturnsOk() throws Exception {
        // Given: DTO를 생성한다.
        LessonEligibilityRequest request = new LessonEligibilityRequest(2, 3);
        // Given: DTO를 JSON 문자열로 변환한다.
        String body = objectMapper.writeValueAsString(request);

        // When & Then: POST 요청과 응답 필드를 검증한다.
        mockMvc.perform(post("/lesson/check-eligibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true));
    }

    @Test
    @DisplayName("[GWT] practice-hours 계산")
    void givenWeeks_whenGetPracticeHours_thenReturnsHours() throws Exception {
        // GET 요청으로 2주 입력에 대한 결과를 확인한다.
        mockMvc.perform(get("/lesson/practice-hours")
                        .param("weeks", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hours").value(10));
    }

    @Test
    @DisplayName("[GWT] practice-hours 검증 실패")
    void givenInvalidWeeks_whenGetPracticeHours_thenReturns400() throws Exception {
        // 0주 입력 시 400이 반환되는지 검증한다.
        mockMvc.perform(get("/lesson/practice-hours")
                        .param("weeks", "0"))
                .andExpect(status().isBadRequest());
    }
}
