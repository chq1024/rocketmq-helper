package com.game.log.engine.ab.factory;

import com.game.log.engine.ab.AbFactory;
import com.game.log.engine.conf.MqMessage;
import com.game.log.engine.conf.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生产者工厂
 * 未使用生产者组，一个生产者与topic之间是一对多的关系
 * 生产者组根据需要取决于broker的数量以及消费端的消费速度（后期可以尝试）
 *
 * @author bk
 */
public class ProducerFactory extends AbFactory<Producer> {

    private final ConcurrentHashMap<String, Producer> producers = new ConcurrentHashMap<>();

    private volatile static ProducerFactory producerFactory;

    private ProducerFactory() {
    }

    public static ProducerFactory instance() {
        if (producerFactory == null) {
            synchronized (ProducerFactory.class) {
                if (producerFactory == null) {
                    producerFactory = new ProducerFactory();
                }
            }
        }
        return producerFactory;
    }

    @Override
    public Producer get(String keywords) {
        return producers.get(keywords);
    }

    @Override
    public void create(MqProperties properties) {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(properties.getProxy())
                .enableSsl(properties.getEnableSsl())
                .build();
        MqProperties.ProducerProperties config = properties.getProducer();
        Map<String, MqProperties.TopicProperties> topicsMap = config.getTopics();
        if (topicsMap.isEmpty()) {
            throw new RuntimeException("请先配置product.topics属性");
        }
        Set<String> topics = topicsMap.keySet();
        ProducerBuilder producerBuilder = provider.newProducerBuilder().setClientConfiguration(configuration).setTopics(topics.toArray(new String[0])).setMaxAttempts(2);
        if (StringUtils.hasText(config.getChecker())) {
            String checker = config.getChecker();
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
                producers.put(topic, producer);
            }
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        HashSet<Producer> producerHashSet = new HashSet<>();
        for (Producer producer : producers.values()) {
            try {
                if (producerHashSet.contains(producer)) {
                    continue;
                }
                producer.close();
                producerHashSet.add(producer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        producerHashSet.clear();
    }

    public void send(String topic, MqMessage message) {

    }

    public void send0(String topic, @Nullable String tag, @Nullable String messageGroup, @Nullable Long deliverTimeMillis, MqMessage message) {

    }

}
