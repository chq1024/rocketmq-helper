package com.game.log.engine.ab.factory;

import com.game.log.engine.ab.AbFactory;
import com.game.log.engine.conf.MqConst;
import com.game.log.engine.conf.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.java.impl.ClientServiceProviderImpl;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拉取消费，同一个消费者组下的订阅关系一致（topic,tag都必须一致）
 * 消息默认已被负载均衡发送到对应的消费者，但消息消费需要自定义负载均衡
 *
 * @author bk
 */
public class SimpleConsumerFactory extends AbFactory<SimpleConsumer> {

    private final ConcurrentHashMap<String, SimpleConsumer> simpleConsumers = new ConcurrentHashMap<>();

    private volatile static SimpleConsumerFactory simpleConsumerFactory;

    private SimpleConsumerFactory() {}

    public static SimpleConsumerFactory instance() {
        if (simpleConsumerFactory == null) {
            synchronized (SimpleConsumerFactory.class) {
                if (simpleConsumerFactory == null) {
                    simpleConsumerFactory = new SimpleConsumerFactory();
                }
            }
        }
        return simpleConsumerFactory;
    }

    public Map<String,SimpleConsumer> consumers() {
        return simpleConsumers;
    }

    @Override
    public SimpleConsumer get(String keywords) {
        return simpleConsumers.get(keywords);
    }

    @Override
    public void create(MqProperties mqProperties) {
        ClientServiceProvider provider = new ClientServiceProviderImpl();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(mqProperties.getProxy())
                .enableSsl(mqProperties.getEnableSsl())
                .build();
        Map<String, MqProperties.ConsumerProperties> consumers = mqProperties.getConsumers();
        try {
            for (Map.Entry<String, MqProperties.ConsumerProperties> config : consumers.entrySet()) {
                String consumerGroupName = config.getKey();
                MqProperties.ConsumerProperties consumerConfig = config.getValue();
                if (!MqConst.CONSUMER_SIMPLE.equalsIgnoreCase(consumerConfig.getType())) {
                    continue;
                }
                SimpleConsumer simpleConsumer = provider.newSimpleConsumerBuilder()
                        .setConsumerGroup(consumerGroupName)
                        .setClientConfiguration(configuration)
                        .setAwaitDuration(Duration.ofSeconds(consumerConfig.getAwaitSeconds()))
                        .setSubscriptionExpressions(parseExpressions(consumerConfig.getTopicTags()))
                        .build();
                simpleConsumers.put(consumerGroupName, simpleConsumer);
            }
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        for (SimpleConsumer simpleConsumer : simpleConsumers.values()) {
            try {
                simpleConsumer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
