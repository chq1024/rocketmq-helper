package com.game.log.engine.consumer.used;

import com.game.log.engine.consumer.ConsumerFactory;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author bk
 */
@Component
public class ConsumerHandlerMessage {

    public void run() {
        RocketMQTemplate template = ConsumerFactory.commentTemplate();
        List<Map> receive = template.receive(Map.class);
        for (Map map : receive) {

        }
    }
}
