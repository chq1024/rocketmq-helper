package com.game.log.engine.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author bk
 */
public class TransformUtil {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static byte[] toByteArray(Map<String,Object> messageBody) {
        String json = "";
        try {
            json = jsonMapper.writeValueAsString(messageBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
