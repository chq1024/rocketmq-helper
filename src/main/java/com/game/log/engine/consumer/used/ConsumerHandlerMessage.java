package com.game.log.engine.consumer.used;

import com.game.log.engine.base.PlayLoad;
import com.game.log.engine.base.TableEnum;
import com.game.log.engine.consumer.ConsumerFactory;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author bk
 */
@Component
public class ConsumerHandlerMessage {

    private static final ThreadPoolExecutor executor;

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
            String action = (String)payload.get("action");
            TableEnum tableEnum = TableEnum.valueOfAction(action);
            String tableName = tableEnum.getTableName();
            System.out.println("已插入消费TableName:" + tableName + " 消息ID:" + payload.get("msgId"));
        };
    }
    @SuppressWarnings("unchecked")
    public void run() {
        RocketMQTemplate template = ConsumerFactory.commentTemplate();
        List<Map> receive = template.receive(Map.class);
        for (Map map : receive) {
            doIt((Message<Map<String, Object>>) map);
        }
    }
}
