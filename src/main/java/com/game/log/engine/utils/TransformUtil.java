package com.game.log.engine.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.log.engine.conf.MqConst;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

    public static String messageTypeTranslate(Boolean isTrans,Boolean isFifo,Boolean isDelay) {
        List<String> messageTypes = new ArrayList<>();
        String prefix = "+message.type=";
        if (Boolean.TRUE.equals(isTrans)) {
            messageTypes.add(MqConst.MESSAGE_TYPE_TRANSACTION);
        }
        if (Boolean.TRUE.equals(isFifo)) {
            messageTypes.add(MqConst.MESSAGE_TYPE_FIFO);
        }
        if (Boolean.TRUE.equals(isDelay)) {
            messageTypes.add(MqConst.MESSAGE_TYPE_FIFO);
        }
        if (messageTypes.isEmpty()) {
            messageTypes.add(MqConst.MESSAGE_TYPE_COMMENT);
        }
        return prefix + String.join(",", messageTypes);
    }
}
