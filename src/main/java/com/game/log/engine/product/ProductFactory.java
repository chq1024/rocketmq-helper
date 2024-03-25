package com.game.log.engine.product;

import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * product工厂，用于创建Template
 * 将template放入FIFO的queue中，通过获取和释放来模仿多线程操作，增加吞吐量
 * @author bk
 */
public class ProductFactory {

    private static final ArrayBlockingQueue<RocketMQTemplate> COMMENT_TEMPLATES = new ArrayBlockingQueue<>(2);

    private static final ArrayBlockingQueue<RocketMQTemplate> SPECIAL_TEMPLATE = new ArrayBlockingQueue<>(1);

    @SuppressWarnings("all")
    public static void regOrReleaseMqTemplate(ProductEnum mqOf,RocketMQTemplate rocketMQTemplate) {
        if (ProductEnum.COMMENT_MQ.equals(mqOf)) {
            COMMENT_TEMPLATES.add(rocketMQTemplate);
        } else if (ProductEnum.SPECIAL_MQ.equals(mqOf)) {
            SPECIAL_TEMPLATE.add(rocketMQTemplate);
        } else {
            throw new RuntimeException("不支持此类型生产者");
        }
    }

    public static RocketMQTemplate commentTemplate() {
        try {
            return COMMENT_TEMPLATES.poll(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Comment生产者忙碌中!");
        }
    }

    public static RocketMQTemplate specialTemplate() {
        try {
            return SPECIAL_TEMPLATE.poll(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Special生产者忙碌中!");
        }
    }

}
