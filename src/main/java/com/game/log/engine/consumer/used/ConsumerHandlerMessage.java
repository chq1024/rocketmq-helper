package com.game.log.engine.consumer.used;

import com.game.log.engine.base.ActionMappingEnum;
import com.game.log.engine.base.ConsumerTemplate;
import com.game.log.engine.base.TemplateFactory;
import com.game.log.engine.utils.ThreadHelper;
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
        while (loop) {
            ConsumerTemplate template = (ConsumerTemplate) TemplateFactory.commentTemplate();
            List<Map> receive = template.receive(Map.class);
            for (Map map : receive) {
                doIt(map);
            }
            template.release();
        }
    }

    public void doIt(Map<String, Object> msg) {
        executor.submit(task(msg));
    }

    public Runnable task(Map<String, Object> msg) {
        return () -> {
            String action = (String) msg.get("action");
            ActionMappingEnum tableEnum = ActionMappingEnum.valueOfAction(action);
            String tableName = tableEnum.getTableName();
//            int random = Integer.parseInt((String) msg.get("batchId"));
//            if (random / 2 == 0) {
//                System.out.println("retry:" + msg.get("msgId"));
//                System.out.println(1 / 0);
//            }
            System.out.println("已插入消费TableName:" + tableName + " 消息ID:" + msg.get("msgId"));
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
