package com.game.log.engine.base;

import com.game.log.engine.factory.AbstractFactory;
import com.game.log.engine.factory.ProducerFactory;
import com.game.log.engine.factory.PushConsumerFactory;
import com.game.log.engine.factory.SimpleConsumerFactory;
import com.game.log.engine.consumer.push.AbstractListener;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

/**
 * @author bk
 */
@Configuration
@EnableConfigurationProperties(MqProperties.class)
@Order(1)
public class RunningInitProcess implements ApplicationRunner {

    private final MqProperties mqProperties;

    private final ProducerFactory producerFactory;

    private final SimpleConsumerFactory simpleConsumerFactory;

    private final PushConsumerFactory pushConsumerFactory;

    public RunningInitProcess(MqProperties mqProperties,ProducerFactory producerFactory,PushConsumerFactory pushConsumerFactory,SimpleConsumerFactory simpleConsumerFactory) {
        this.mqProperties = mqProperties;
        this.producerFactory = producerFactory;
        this.pushConsumerFactory = pushConsumerFactory;
        this.simpleConsumerFactory = simpleConsumerFactory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. 加载producer
        MqProperties.ProducerProperties producer = mqProperties.getProducer();
        Integer initNum = producer.getInitNum();
        List<String> topics = producer.getTopics();
        for (int i = 0; i < initNum; i++) {
            producerFactory.create(null, topics.toArray(new String[0]));
        }
        // 2. 加载consumer
        Map<String, MqProperties.ConsumerProperties> consumers = mqProperties.getConsumers();
        for (String consumerGroup : consumers.keySet()) {
            MqProperties.ConsumerProperties consumerProperties = consumers.get(consumerGroup);
            String type = consumerProperties.getType();
            AbstractFactory factory = findFactory(type);
            if (factory instanceof PushConsumerFactory) {
                String listener = consumerProperties.getListener();
                if (!StringUtils.hasText(listener)) {
                    throw new RuntimeException("请配置消费者监听器");
                }
                Class<?> listenerClazz = Class.forName(listener);
                AbstractListener newListener = (AbstractListener) listenerClazz.getDeclaredConstructor().newInstance();
                for (int i = 0; i < consumerProperties.getInitNum(); i++) {
                    pushConsumerFactory.create(consumerProperties.getTopicTags(),consumerGroup,consumerProperties.getThreadNum(),newListener);
                }
            } else {
                simpleConsumerFactory.create(consumerProperties.getTopicTags(), consumerGroup, 10);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        producerFactory.destroy();
        simpleConsumerFactory.destroy();
        pushConsumerFactory.destroy();
    }

    public AbstractFactory findFactory(String type) {
        if ("simple".equals(type)) {
            return simpleConsumerFactory;
        } else if ("push".equals(type)) {
            return pushConsumerFactory;
        } else {
            throw new RuntimeException("错误的消费者类型！");
        }
    }
}
