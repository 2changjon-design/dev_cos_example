package com.example.study.aop.support;

import com.example.study.common.ApiResponse;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class AspectLogHelper {

    private AspectLogHelper() {
    }

    public static String summarize(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof ApiResponse<?> apiResponse) {
            return "ApiResponse(result=" + apiResponse.getResult()
                    + ", data=" + summarize(apiResponse.getData()) + ")";
        }
        if (value instanceof Collection<?> collection) {
            return "Collection(size=" + collection.size() + ")";
        }
        if (value.getClass().isArray()) {
            return "Array(length=" + Array.getLength(value) + ")";
        }
        if (value instanceof Map<?, ?> map) {
            return "Map(size=" + map.size() + ")";
        }
        return Objects.toString(value);
    }
}

