package com.example.study.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    Boolean result; // success
    Error error;
    T data;

    // 성공응답 - 데이터 없을 경우
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .result(true)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .result(true)
                .data(data)
                .build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String code, String errorMessage) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .result(false)
                .error(Error.of(code, errorMessage))
                .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String code, String errorMessage) {
        return badRequest(code, errorMessage, Collections.emptyList());
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String code, String errorMessage, List<String> details) {
        return ResponseEntity.badRequest().body(ApiResponse.<T>builder()
                .result(false)
                .error(Error.of(code, errorMessage, details))
                .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> serverError(String code, String errorMessage) {
        return ResponseEntity.status(500).body(ApiResponse.<T>builder()
                .result(false)
                .error(Error.of(code, errorMessage))
                .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(success(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created() {
        return ResponseEntity.status(HttpStatus.CREATED).body(success());
    }

    public record Error(String code, String message, List<String> details) {
        public static Error of(String code, String message) {
            return new Error(code, message, Collections.emptyList());
        }

        public static Error of(String code, String message, List<String> details) {
            return new Error(code, message, details == null ? Collections.emptyList() : List.copyOf(details));
        }
    }

}
