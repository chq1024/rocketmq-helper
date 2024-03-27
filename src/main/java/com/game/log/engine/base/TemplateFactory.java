package com.game.log.engine.base;

import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author bk
 */
public class TemplateFactory {

    private static final ArrayBlockingQueue<ConsumerTemplate> CONSUMER_COMMENT_TEMPLATES = new ArrayBlockingQueue<>(2);

    private static final ArrayBlockingQueue<ConsumerTemplate> CONSUMER_TRANSACTION_TEMPLATE = new ArrayBlockingQueue<>(1);

    private static final ArrayBlockingQueue<ProducerTemplate> PRODUCT_TEMPLATE = new ArrayBlockingQueue<>(2);

    @SuppressWarnings("all")
    public static void regOrReleaseMqTemplate(TemplateMappingEnum tm, RocketMQTemplate rocketMQTemplate) {
        if (TemplateMappingEnum.MSG_COMMENT_MQ.equals(tm)) {
            CONSUMER_COMMENT_TEMPLATES.add((ConsumerTemplate) rocketMQTemplate);
        } else if (TemplateMappingEnum.MSG_TRANSACTION_MQ.equals(tm)) {
            CONSUMER_TRANSACTION_TEMPLATE.add((ConsumerTemplate) rocketMQTemplate);
        } else if (TemplateMappingEnum.PRODUCT_TEMPLATE.equals(tm)) {
            PRODUCT_TEMPLATE.add((ProducerTemplate) rocketMQTemplate);
        } else {
            throw new RuntimeException("不支持此类型生产者");
        }
    }

    public static RocketMQTemplate commentTemplate() {
        try {
            return CONSUMER_COMMENT_TEMPLATES.poll(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Comment消费者组忙碌中!");
        }
    }

    public static RocketMQTemplate transactionTemplate() {
        try {
            return CONSUMER_TRANSACTION_TEMPLATE.poll(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Transaction消费者组忙碌中!");
        }
    }

    public static ProducerTemplate producerTemplate() {
        try {
            return PRODUCT_TEMPLATE.poll(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("生产者组忙碌中!");
        }
    }

}
