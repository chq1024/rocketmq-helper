package com.game.log.engine.factory;

import com.game.log.engine.base.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bk
 */
@Configuration
public class ProducerFactory extends AbstractFactory {

    private final ConcurrentHashMap<String, Producer> producers = new ConcurrentHashMap<>();

    public void create(String proxy, Boolean enableSsl, MqProperties.ProducerProperties properties) {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(proxy)
                .enableSsl(enableSsl)
                .build();
        Map<String, MqProperties.TopicProperties> topicsMap = properties.getTopics();
        if (topicsMap.isEmpty()) {
            throw new RuntimeException("请先配置product.topics属性");
        }
        Set<String> topics = topicsMap.keySet();
        ProducerBuilder producerBuilder = provider.newProducerBuilder().setClientConfiguration(configuration).setTopics(topics.toArray(new String[0])).setMaxAttempts(2);
        if (StringUtils.hasText(properties.getChecker())) {
            String checker = properties.getChecker();
            try {
                Class<?> transactionCheckerClazz = Class.forName(checker);
                TransactionChecker transactionChecker = (TransactionChecker) transactionCheckerClazz.getDeclaredConstructor().newInstance();
                producerBuilder.setTransactionChecker(transactionChecker);
            } catch (Exception e) {
                throw new RuntimeException("product.checker属性配置异常");
            }
        }
        try {
            Producer producer = producerBuilder.build();
            for (String topic : topics) {
                producers.put(topic,producer);
            }
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        for (Producer producer : producers.values()) {
            try {
                // TODO 会被重复关闭
                producer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
