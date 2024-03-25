package com.game.log.engine.consumer;

import org.springframework.messaging.Message;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author bk
 */
public class MessageHandler {

    private static ThreadPoolExecutor executor;

    static {
        ThreadFactory factory = r -> {
            Thread thread = new Thread(r);
            thread.setName("");
            return thread;
        };
        executor = new ThreadPoolExecutor(2, 10, 60000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                factory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void doIt(Message<Map<String,Object>> msg) {
        executor.submit(task(msg));
    }

    public Runnable task(Message<Map<String,Object>> msg) {
        return ()->{
            Map<String, Object> payload = msg.getPayload();
        };
    }
}
