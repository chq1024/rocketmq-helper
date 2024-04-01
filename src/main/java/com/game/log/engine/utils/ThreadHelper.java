package com.game.log.engine.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bk
 */
public class ThreadHelper {

    private static final AtomicInteger incr = new AtomicInteger(0);

    private static volatile ThreadPoolExecutor executor;

    // 参数可以在外部配置
    private static int core = 2;
    private static int max = 10;
    private static int second = 30;
    private static int block = 100;
    private static int newThreadId() {
        return incr.incrementAndGet();
    }

    public static void execute(Runnable runnable) {
        before();
        executor.execute(runnable);
    }

    public static <T> T submit(Callable<T> callable) {
        before();
        Future<T> submit = executor.submit(callable);
        try {
            return submit.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private static void before() {
        if (executor == null) {
            synchronized (ThreadHelper.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(core, max, second, TimeUnit.SECONDS, new ArrayBlockingQueue<>(block), r -> {
                        Thread thread = new Thread(r);
                        thread.setName("Game-Thread-" + newThreadId());
                        return thread;
                    }, new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
        }
    }
}
