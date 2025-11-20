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
class CartControllerGWTTest {

    @Autowired
    MockMvc mockMvc; // MockMvc 주입

    @Autowired
    ObjectMapper objectMapper; // ObjectMapper 주입

    @Test
    @DisplayName("[GWT] 아이템 추가 후 summary 확인")
    void givenItem_whenAdd_thenSummaryReflects() throws Exception {
        AddCartItemRequest request = new AddCartItemRequest("노트", 2000, 1); // Given DTO
        String body = objectMapper.writeValueAsString(request); // DTO 직렬화

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].itemName").value("노트"));
    }

    @Test
    @DisplayName("[GWT] 삭제 성공 후 summary 갱신")
    void givenExistingItem_whenDelete_thenSummaryUpdates() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddCartItemRequest("펜", 500, 1))))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemName\":\"펜\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuantity").value(0));
    }

    @Test
    @DisplayName("[GWT] summary API는 현재 상태를 반환")
    void givenCart_whenGetSummary_thenMatchesState() throws Exception {
        mockMvc.perform(get("/cart/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(0));
    }
}
