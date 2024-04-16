package com.game.log.engine.factory;

import com.game.log.engine.base.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.java.impl.ClientServiceProviderImpl;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 监听推送的消息，此类消费者创建后自动监听，无需释放共用
 *
 * @author bk
 */
@Configuration
public class PushConsumerFactory extends AbstractFactory {

    private final MqProperties mqProperties;
    private final ArrayBlockingQueue<PushConsumer> pushConsumers = new ArrayBlockingQueue<>(1);

    public PushConsumerFactory(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    public void create(List<String> expressions, String consumerGroup, Integer maxThreadNum, MessageListener listener) throws ClientException {
        ClientServiceProvider provider = new ClientServiceProviderImpl();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(mqProperties.getProxy())
                .enableSsl(mqProperties.getEnableSsl())
                .build();

        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(configuration)
                .setConsumerGroup(consumerGroup)
                .setMessageListener(listener)
                .setConsumptionThreadCount(maxThreadNum)
                .setSubscriptionExpressions(parseExpressions(expressions))
                .setMaxCacheMessageCount(1024)
                .setMaxCacheMessageSizeInBytes(64 * 1024 * 1024)
                .build();
        pushConsumers.add(pushConsumer);
    }

    public void destroy() {
        try {
            for (PushConsumer pushConsumer : pushConsumers) {
                pushConsumer.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
