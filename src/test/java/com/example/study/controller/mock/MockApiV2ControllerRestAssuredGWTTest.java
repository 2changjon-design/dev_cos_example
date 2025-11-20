package com.example.study.controller.mock;

// Rest Assured의 Matcher를 사용해 응답을 검증한다.
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

// 랜덤 포트로 실제 내장 서버를 띄워 Rest Assured가 호출하도록 한다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MockApiV2ControllerRestAssuredGWTTest {

    // 테스트 서버 포트를 주입받는다.
    @LocalServerPort
    int port;

    // 각 테스트 전에 Rest Assured의 기본 설정을 맞춰준다.
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/mock/v2";
    }

    // 시드 데이터 목록을 조회하는 GWT 테스트다.
    @Test
    @DisplayName("[RestAssured] 목록 조회")
    void givenSeed_whenGetProducts_thenReturnList() {
        RestAssured.given().log().all()
                .when()
                .get("/products")
                .then().log().all()
                .statusCode(200)
                .body("result", equalTo(true))
                .body("data.size()", greaterThanOrEqualTo(3));
    }

    // 상품 생성 성공 케이스를 검증한다.
    @Test
    @DisplayName("[RestAssured] 상품 생성")
    void givenValidBody_whenPostProducts_then201() {
        String requestBody = """
                {
                  \"name\": \"Rest 상품\",
                  \"price\": 55000,
                  \"stock\": 11
                }
                """;

        RestAssured.given().log().body()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/products")
                .then().log().body()
                .statusCode(201)
                .body("data.name", equalTo("Rest 상품"));
    }

    // 잘못된 입력 시 400이 반환되는지 확인한다.
    @Test
    @DisplayName("[RestAssured] 잘못된 상품 생성")
    void givenInvalidBody_whenPostProducts_then400() {
        String requestBody = """
                {
                  \"name\": \"Invalid\",
                  \"price\": 0,
                  \"stock\": -1
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/products")
                .then()
                .statusCode(400);
    }

    // 재고를 음수로 만드는 요청일 때 409 에러를 검증한다.
    @Test
    @DisplayName("[RestAssured] 재고 부족")
    void givenNegativeDelta_whenPatchStock_then409() {
        String requestBody = """
                {
                  \"quantityDelta\": -999
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/products/{id}/stock", 1)
                .then()
                .statusCode(409)
                .body("error.errorCode", equalTo("INSUFFICIENT_STOCK"));
    }

    // 삭제한 상품을 다시 삭제하면 409가 내려오는지 확인한다.
    @Test
    @DisplayName("[RestAssured] 삭제 후 재삭제")
    void givenDeletedProduct_whenDeleteAgain_then409() {
        RestAssured.when()
                .delete("/products/{id}", 1)
                .then()
                .statusCode(200)
                .body("data.deleted", equalTo(true));

        RestAssured.when()
                .delete("/products/{id}", 1)
                .then()
                .statusCode(409)
                .body("error.errorCode", equalTo("ALREADY_DELETED"));
    }
}
