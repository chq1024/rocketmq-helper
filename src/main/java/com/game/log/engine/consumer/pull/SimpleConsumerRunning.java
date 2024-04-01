package com.game.log.engine.consumer.pull;

import com.game.log.engine.factory.SimpleConsumerFactory;
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

/**
 * @author bk
 */
@Component
@Order(2)
public class SimpleConsumerRunning implements ApplicationRunner {

    private SimpleConsumerFactory factory;

    public SimpleConsumerRunning(SimpleConsumerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> groups = factory.simpleConsumerGroup();
        for (String group : groups) {
            ThreadHelper.execute(()->{
                while (true) {
                    SimpleConsumer simpleConsumer = factory.get(group);
                        // 每条消息应该作为单个线程处理
                    List<MessageView> receive = null;
                    try {
                        receive = simpleConsumer.receive(10, Duration.ofSeconds(10));
                    } catch (ClientException e) {
                        throw new RuntimeException(e);
                    }
                    for (MessageView messageView : receive) {
                        ThreadHelper.execute(()->{
                            try {
                                simpleConsumer.ack(messageView);
                            } catch (ClientException e) {
                                throw new RuntimeException(e);
                            } finally {
                                factory.release(group,simpleConsumer);
                            }
                        });
                    }
                }
            });
        }
    }
}
