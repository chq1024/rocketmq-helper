package com.game.log.engine.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author bk
 */
public class DateUtil {

    public static long timestamp() {
        return timestamp(LocalDateTime.now(ZoneOffset.of("+8")));
    }

    private static long timestamp(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.of("+8"));
    }
}
