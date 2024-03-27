package com.game.log.engine.consumer.used;

import com.game.log.engine.base.ActionMappingEnum;
import com.game.log.engine.base.ConsumerTemplate;
import com.game.log.engine.base.TemplateFactory;
import com.game.log.engine.utils.ThreadHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author bk
 */
@Component
@Slf4j
public class ConsumerHandlerMessage implements ApplicationRunner {

    private static final ThreadPoolExecutor executor;

    private static  boolean loop = true;

    static {
        ThreadFactory factory = r -> {
            Thread thread = new Thread(r);
            thread.setName("Thread-" + ThreadHelper.newThreadId());
            return thread;
        };
        executor = new ThreadPoolExecutor(2, 10, 60000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100), factory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 拉取消息消费
     */
    @SuppressWarnings("unchecked")
    public void run() {
        Thread thread = new Thread(() -> {
            while (loop) {
                ConsumerTemplate template = (ConsumerTemplate) TemplateFactory.commentTemplate();
                List<Map> receive = template.receive(Map.class);
                try {
                    for (Map map : receive) {
                        doIt(template.getIdx(),map);
                    }
                } catch (Exception e) {
                    log.error("消费者消费失败：" + e.getMessage());
                } finally {
                    template.release();
                }
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    public void doIt(String idx,Map<String, Object> msg) {
        executor.submit(task(idx,msg));
    }

    public Runnable task(String idx,Map<String, Object> msg) {
        return () -> {
            String action = (String) msg.get("action");
            ActionMappingEnum tableEnum = ActionMappingEnum.valueOfAction(action);
            String tableName = tableEnum.getTableName();
            System.out.println("已被" + idx + "消费TableName:" + tableName + " 消息ID:" + msg.get("msgId"));
        };
    }

    @PreDestroy
    public void destroy() {
        loop = false;
        executor.shutdown();
    }

    @Override
    public void run(ApplicationArguments args) {
        run();
    }
}
