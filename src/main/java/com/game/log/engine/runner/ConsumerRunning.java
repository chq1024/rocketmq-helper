package com.game.log.engine.runner;

import com.game.log.engine.ab.MessageHandler;
import com.game.log.engine.ab.factory.MessageFactory;
import com.game.log.engine.ab.factory.MessageHandlerFactory;
import com.game.log.engine.conf.MqProperties;
import com.game.log.engine.ab.factory.SimpleConsumerFactory;
import com.game.log.engine.utils.ThreadHelper;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bk
 */
@Component
@Order(2)
public class ConsumerRunning implements ApplicationRunner {

    private final MqProperties mqProperties;

    public ConsumerRunning(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        // pushConsumer会根据listener自动消费
        // simpleConsumer需要手写循环消费
        Map<String, SimpleConsumer> consumers = SimpleConsumerFactory.instance().consumers();
        AtomicInteger idx = new AtomicInteger(1);
        for (Map.Entry<String, SimpleConsumer> entry : consumers.entrySet()) {
            String group = entry.getKey();
            SimpleConsumer consumer = entry.getValue();
            MqProperties.ConsumerProperties config = mqProperties.getConsumers().get(group);
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        List<MessageView> receive = consumer.receive(config.getReceiveNum(), Duration.ofSeconds(config.getInvisibleSeconds()));
                        MessageHandler handler = MessageFactory.handler().get(group);
                        for (MessageView messageView : receive) {
                            handler.handler(messageView);
                            consumer.ack(messageView);
                        }
                    } catch (ClientException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.setName("SIMPLE_CONSUMER_THREAD_" + idx.getAndIncrement());
            thread.setDaemon(true);
            thread.start();
        }
    }
}
