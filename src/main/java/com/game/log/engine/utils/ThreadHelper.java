package com.game.log.engine.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bk
 */
public class ThreadHelper {

    private static AtomicInteger incr = new AtomicInteger(0);

    public static int newThreadId() {
        return incr.incrementAndGet();
    }
}
