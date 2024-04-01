package com.game.log.engine.factory;

import com.game.log.engine.base.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.java.impl.ClientServiceProviderImpl;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 拉取消费，同一个消费者组下的订阅关系一致（topic,tag都必须一致）
 * 消息默认已被负载均衡发送到对应的消费者，但消息消费需要自定义负载均衡
 *
 * @author bk
 */
@Configuration
public class SimpleConsumerFactory extends AbstractFactory {

    private final MqProperties mqProperties;

    private final ConcurrentHashMap<String, ArrayBlockingQueue<SimpleConsumer>> simpleConsumers = new ConcurrentHashMap<>(1);

    public SimpleConsumerFactory(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    public SimpleConsumer create(List<String> expressions, String consumerGroup, Integer awaitSecond) throws ClientException {
        ClientServiceProvider provider = new ClientServiceProviderImpl();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(mqProperties.getNameserver())
                .enableSsl(mqProperties.getEnableSsl())
                .build();

        Map<String, FilterExpression> expressionsMap = parseExpressions(expressions);
        SimpleConsumer simpleConsumer = provider.newSimpleConsumerBuilder().
                setClientConfiguration(configuration).
                setConsumerGroup(consumerGroup).
                setAwaitDuration(Duration.ofSeconds(awaitSecond))
                .setSubscriptionExpressions(expressionsMap)
                .build();

        simpleConsumers.compute(consumerGroup, (k, v) -> {
            if (v == null) {
                v = new ArrayBlockingQueue<>(1);
                v.add(simpleConsumer);
            } else {
                v.add(simpleConsumer);
            }
            return v;
        });

        return simpleConsumer;
    }

    public SimpleConsumer get(String consumerGroup) {
        if (!simpleConsumers.containsKey(consumerGroup)) {
            throw new RuntimeException("当前不存在该消费者组的消费者！");
        }
        try {
            return simpleConsumers.get(consumerGroup).take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> simpleConsumerGroup() {
        return new ArrayList<>(simpleConsumers.keySet());
    }


    public void destroy() {
        ArrayBlockingQueue<SimpleConsumer> queue = simpleConsumers.values().stream().reduce(new ArrayBlockingQueue<>(1), (a, b) -> {
            a.addAll(b);
            return a;
        });
        try {
            for (SimpleConsumer simpleConsumer : queue) {
                simpleConsumer.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void release(String group, SimpleConsumer simpleConsumer) {
        simpleConsumers.computeIfPresent(group,(k,v)-> {
            v.add(simpleConsumer);
            return v;
        });
    }
}
