package com.game.log.engine.factory;

import com.game.log.engine.base.MqProperties;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author bk
 */
@Configuration
public class ProducerFactory extends AbstractFactory {

    private final MqProperties mqProperties;

    private final ArrayBlockingQueue<Producer> producers;

    ProducerFactory(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
        this.producers = new ArrayBlockingQueue<>(mqProperties.getProducer().getInitNum());
    }

    public Producer create(@Nullable TransactionChecker checker, String... topics) throws ClientException {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(mqProperties.getProxy())
                .enableSsl(mqProperties.getEnableSsl())
                .build();
        ProducerBuilder producerBuilder =  provider.newProducerBuilder().setClientConfiguration(configuration).setTopics(topics).setMaxAttempts(2);
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
