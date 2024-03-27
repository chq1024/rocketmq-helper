package com.game.log.engine.factory;

import com.game.log.engine.base.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.java.impl.ClientServiceProviderImpl;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拉取消费，同一个消费者组下的订阅关系一致（topic,tag都必须一致）
 * 消息默认已被负载均衡发送到对应的消费者，但消息消费需要自定义负载均衡
 * @author bk
 */
@Configuration
public class SimpleConsumerFactory  {

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

        simpleConsumers.compute(consumerGroup,(k,v)->{
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
        if (!simpleConsumers.containsKey(consumerGroup)){
            throw new RuntimeException("当前不存在该消费者组的消费者！");
        }
        try {
            return simpleConsumers.get(consumerGroup).take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String,FilterExpression> parseExpressions(List<String> expressions) {
        Map<String,FilterExpression> res = new HashMap<>(expressions.size());
        for (String expression : expressions) {
            String[] arr = expression.split(":");
            String topic = arr[0];
            List<String> tags = new ArrayList<>(arr.length - 1);
            for (int i = 1; i < arr.length; i++) {
                tags.add(arr[i]);
            }
            if (res.containsKey(topic)) {
                throw new RuntimeException("配置有误,同一个消费者中不能出现相同的topic");
            }
            res.put(topic,new FilterExpression(String.join("|", tags)));
        }
        return res;
    }

    @PreDestroy
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
}
