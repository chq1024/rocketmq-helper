package com.game.log.engine.utils;

import java.util.UUID;

/**
 * @author bk
 */
public class IdHelper {

    public static String msgId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
