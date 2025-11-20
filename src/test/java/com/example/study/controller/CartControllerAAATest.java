package com.example.study.controller;

import com.example.study.controller.dto.AddCartItemRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CartControllerAAATest {

    @Autowired
    MockMvc mockMvc; // MockMvc 인스턴스를 주입받는다.

    @Autowired
    ObjectMapper objectMapper; // JSON 직렬화를 위한 ObjectMapper다.

    @Test
    @DisplayName("[AAA] 아이템 추가 API는 요약 정보를 반환한다")
    void addItem_shouldReturnSummary() throws Exception {
        AddCartItemRequest request = new AddCartItemRequest("펜", 500, 2); // 요청 DTO 생성
        String body = objectMapper.writeValueAsString(request); // DTO를 JSON 문자열로 변환

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(1000));
    }

    @Test
    @DisplayName("[AAA] 삭제 API는 존재하지 않는 아이템에 대해 400을 반환한다")
    void removeItem_shouldReturnBadRequest_whenMissing() throws Exception {
        String body = "{\"itemName\":\"없는아이템\"}"; // 잘못된 삭제 요청 Body

        mockMvc.perform(delete("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[AAA] summary API는 빈 장바구니를 반환한다")
    void summary_shouldReturnEmptyCart() throws Exception {
        mockMvc.perform(get("/cart/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.totalQuantity").value(0))
                .andExpect(jsonPath("$.totalPrice").value(0));
    }
}
