package com.game.log.engine.consumer;

import com.game.log.engine.product.ProductEnum;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author bk
 */
public class ConsumerFactory {

    private static final ArrayBlockingQueue<RocketMQTemplate> COMMENT_TEMPLATES = new ArrayBlockingQueue<>(2);

    private static final ArrayBlockingQueue<RocketMQTemplate> SPECIAL_TEMPLATE = new ArrayBlockingQueue<>(1);

    @SuppressWarnings("all")
    public static void regOrReleaseMqTemplate(ProductEnum mqOf, RocketMQTemplate rocketMQTemplate) {
        if (ProductEnum.COMMENT_MQ.equals(mqOf)) {
            COMMENT_TEMPLATES.add(rocketMQTemplate);
        } else if (ProductEnum.SPECIAL_MQ.equals(mqOf)) {
            SPECIAL_TEMPLATE.add(rocketMQTemplate);
        } else {
            throw new RuntimeException("不支持此类型生产者");
        }
    }

    public static RocketMQTemplate commentTemplate() {
       return COMMENT_TEMPLATES.poll();
    }

    public static RocketMQTemplate specialTemplate() {
        return SPECIAL_TEMPLATE.poll();
    }
}
