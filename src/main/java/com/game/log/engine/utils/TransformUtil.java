package com.game.log.engine.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.log.engine.conf.MqMessage;

import java.nio.charset.StandardCharsets;

/**
 * @author bk
 */
public class TransformUtil {

    private static final ObjectMapper jsonMapper;

    static {
        jsonMapper = new ObjectMapper();
    }

    public static byte[] toByteArray(MqMessage message) {
        String json = "";
        try {
            json = jsonMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
