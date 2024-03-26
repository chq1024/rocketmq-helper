package com.game.log.engine.product.templates;

import com.game.log.engine.base.MqPropertiesConfiguration;
import com.game.log.engine.base.ProducerTemplate;
import com.game.log.engine.base.TemplateFactory;
import com.game.log.engine.base.TemplateMappingEnum;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.apache.rocketmq.client.java.example.ProducerSingleton;
import org.apache.rocketmq.client.java.impl.producer.ProducerBuilderImpl;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * 消息生产者模板，@ExtRocketMQTemplateConfiguration每个Template对应一个product
 * 如果想建立生产者集群，需要使用同样的配置注入多个product
 * @author bk
 */

@Component
public class OneProduct extends ProducerTemplate {

    @Autowired
    private MqPropertiesConfiguration mqProperties;

    @PostConstruct
    public void inited() {
        register();
    }

    @Override
    public void register() throws ClientException {
        ProducerBuilder producerBuilder = new ProducerBuilderImpl();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .enableSsl(false)
                .setEndpoints(mqProperties.getNameserver())
                .setRequestTimeout(Duration.of(60000, ChronoUnit.MILLIS))
                .build();
        producerBuilder.setClientConfiguration(clientConfiguration);
        producerBuilder.setMaxAttempts(2);
        producerBuilder.setTopics("A","B");
        Producer producer = producerBuilder.build();
        TemplateFactory

    }

    @Override
    public void release() {

    }
}
