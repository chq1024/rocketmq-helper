package com.game.log.engine.ab.factory;

import com.game.log.engine.ab.AbFactory;
import com.game.log.engine.conf.MqConst;
import com.game.log.engine.conf.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.java.impl.ClientServiceProviderImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听推送的消息，此类消费者创建后自动监听，无需释放共用
 *
 * @author bk
 */
public class PushConsumerFactory extends AbFactory<PushConsumer> {

    private final ConcurrentHashMap<String, PushConsumer> pushConsumers = new ConcurrentHashMap<>();

    private volatile static PushConsumerFactory pushConsumerFactory;

    private PushConsumerFactory() {}

    public static PushConsumerFactory instance() {
        if (pushConsumerFactory == null) {
            synchronized (PushConsumerFactory.class) {
                if (pushConsumerFactory == null) {
                    pushConsumerFactory = new PushConsumerFactory();
                }
            }
        }
        return pushConsumerFactory;
    }

    @Override
    public PushConsumer get(String consumerGroup) {
        return pushConsumers.get(consumerGroup);
    }

    @Override
    public void create(MqProperties properties) {
        ClientServiceProvider provider = new ClientServiceProviderImpl();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(properties.getProxy())
                .enableSsl(properties.getEnableSsl())
                .build();
        try {
            Map<String, MqProperties.ConsumerProperties> consumers = properties.getConsumers();
            for (Map.Entry<String, MqProperties.ConsumerProperties> config : consumers.entrySet()) {
                String consumerGroupName = config.getKey();
                MqProperties.ConsumerProperties consumerConfig = config.getValue();
                if (!MqConst.CONSUMER_PUSH.equals(consumerConfig.getType())) {
                    continue;
                }

                PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                        .setClientConfiguration(configuration)
                        .setConsumerGroup(consumerGroupName)
                        .setMessageListener(MessageListenerFactory.instance().get(consumerGroupName))
                        .setConsumptionThreadCount(consumerConfig.getThreadNum())
                        .setMaxCacheMessageCount(consumerConfig.getMessageCount())
                        .setMaxCacheMessageSizeInBytes(consumerConfig.getMessageSize())
                        .setSubscriptionExpressions(parseExpressions(consumerConfig.getTopicTags()))
                        .build();
                pushConsumers.put(consumerGroupName, pushConsumer);
            }
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            for (PushConsumer pushConsumer : pushConsumers.values()) {
                pushConsumer.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
