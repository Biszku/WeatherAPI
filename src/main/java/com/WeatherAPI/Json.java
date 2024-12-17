package com.WeatherAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Json {
    private static ObjectMapper objectMapper = getDefaultObjectMapper();

    private static ObjectMapper getDefaultObjectMapper() {
        return new ObjectMapper();
    }

    public static JsonNode parse(String src) {
        try {
            return objectMapper.readTree(src);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <A> A fromJson(JsonNode node, Class<A> expectedClass) {
        try {
            return objectMapper.treeToValue(node, expectedClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String stringify(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}
