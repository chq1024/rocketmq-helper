package com.game.log.engine.factory;

import com.game.log.engine.base.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.apache.rocketmq.client.java.impl.ClientServiceProviderImpl;
import org.apache.rocketmq.client.java.impl.producer.ProducerBuilderImpl;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author bk
 */
@Configuration
public class ProducerFactory {

    private final MqProperties mqProperties;

    private final ArrayBlockingQueue<Producer> producers = new ArrayBlockingQueue<>(1);

    ProducerFactory(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    public Producer create(@Nullable TransactionChecker checker, String... topics) {
        ClientServiceProvider provider = new ClientServiceProviderImpl();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(mqProperties.getNameserver())
                .enableSsl(mqProperties.getEnableSsl())
                .build();
        ProducerBuilderImpl producerBuilder = (ProducerBuilderImpl) provider.newProducerBuilder().setClientConfiguration(configuration).setTopics(topics).setMaxAttempts(2);
        if (checker != null) {
            producerBuilder.setTransactionChecker(checker);
        }
        Producer producer = producerBuilder.build();
        producers.add(producer);
        return producer;
    }

    public Producer get() {
        try {
            return producers.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void release(Producer producer) {
        producers.add(producer);
    }

    @PreDestroy
    public void destroy() {
        for (Producer producer : producers) {
            try {
                producer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
