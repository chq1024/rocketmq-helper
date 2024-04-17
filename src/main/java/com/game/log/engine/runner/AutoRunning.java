package com.game.log.engine.runner;

import com.game.log.engine.ab.MessageHandler;
import com.game.log.engine.ab.factory.*;
import com.game.log.engine.anno.ConsumerHandler;
import com.game.log.engine.anno.ConsumerListener;
import com.game.log.engine.conf.MqProperties;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * @author bk
 */
@Configuration
@EnableConfigurationProperties(MqProperties.class)
@Order(1)
public class AutoRunning implements ApplicationRunner, ApplicationContextAware {

    private final MqProperties mqProperties;

    private ApplicationContext context;

    public AutoRunning(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 0. 根据注解加载messageHandler和messageListener
        Map<String, Object> handlersMap = context.getBeansWithAnnotation(ConsumerHandler.class);
        for (Object value : handlersMap.values()) {
            MessageHandler handler =  (MessageHandler) value;
            ConsumerHandler annotation = handler.getClass().getAnnotation(ConsumerHandler.class);
            MessageHandlerFactory.instance().set(annotation.groupName(),handler);
        }
        Map<String, Object> listenersMap = context.getBeansWithAnnotation(ConsumerListener.class);
        for (Object value : listenersMap.values()) {
            MessageListener listener =  (MessageListener) value;
            ConsumerListener annotation = listener.getClass().getAnnotation(ConsumerListener.class);
            MessageListenerFactory.instance().set(annotation.groupName(),listener);
        }
        // 1. 加载producer
        ProducerFactory.instance().create(mqProperties);
        // 2. 加载consumer
        PushConsumerFactory.instance().create(mqProperties);
        SimpleConsumerFactory.instance().create(mqProperties);
    }

    @PreDestroy
    public void destroy() {
        ProducerFactory.instance().destroy();
        PushConsumerFactory.instance().destroy();
        SimpleConsumerFactory.instance().destroy();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
