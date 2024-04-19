package com.game.log.engine.runner;

import com.game.log.engine.ab.MessageHandler;
import com.game.log.engine.ab.factory.*;
import com.game.log.engine.anno.ConsumerHandler;
import com.game.log.engine.anno.ConsumerListener;
import com.game.log.engine.conf.MqProperties;
import com.game.log.engine.utils.CacheUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.srvutil.ServerUtil;
import org.apache.rocketmq.tools.command.SubCommandException;
import org.apache.rocketmq.tools.command.topic.UpdateTopicSubCommand;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * 本类用于创建主题、生产者、消费者
 * 配置消费者监听、处理类
 * @author bk
 */
@Configuration
@EnableConfigurationProperties(MqProperties.class)
@Order(1)
public class AutoRunning implements ApplicationRunner, ApplicationContextAware {

    private final MqProperties mqProperties;

    private ApplicationContext context;

    public AutoRunning(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        // -1. 自动创建主题，且自动修改主题配置
        Map<String, MqProperties.TopicProperties> topics = mqProperties.getProducer().getTopics();
        System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, mqProperties.getNameAddr());
        for (Map.Entry<String, MqProperties.TopicProperties> entry : topics.entrySet()) {
            String topicName = entry.getKey();
            MqProperties.TopicProperties topicConfig = entry.getValue();
            UpdateTopicSubCommand command = new UpdateTopicSubCommand();
            String[] params = new String[] {
                    "-c " + mqProperties.getClusterName(),
                    "-t " + topicName,
                    "-o " + topicConfig.getOrder(),
                    "-n " + mqProperties.getNameAddr(),
                    "-r 3",
                    "-w 3",
                    "-p 6",
                    "-a +message.type=" + topicConfig.getType().toUpperCase(),
            };
            Options options = ServerUtil.buildCommandlineOptions(new Options());
            final Options updateTopicOptions = command.buildCommandlineOptions(options);
            // 切勿将PosixParser修改为DefaultParser，DefaultParser无法解析-a属性值
            CommandLine mqadmin = ServerUtil.parseCmdLine("mqadmin", params, updateTopicOptions, new PosixParser());
            try {
                command.execute(mqadmin,updateTopicOptions,null);
                CacheUtil.setTmtCache(topicName,topicConfig.getType());
            } catch (SubCommandException e) {
                throw new RuntimeException(e);
            }
        }

        // 0. 根据注解加载messageHandler和messageListener
        Map<String, Object> handlersMap = context.getBeansWithAnnotation(ConsumerHandler.class);
        for (Object value : handlersMap.values()) {
            MessageHandler handler =  (MessageHandler) value;
            ConsumerHandler annotation = handler.getClass().getAnnotation(ConsumerHandler.class);
            MessageHandlerFactory.instance().set(annotation.groupName(),handler);
        }
        Map<String, Object> listenersMap = context.getBeansWithAnnotation(ConsumerListener.class);
        for (Object value : listenersMap.values()) {
            MessageListener listener =  (MessageListener) value;
            ConsumerListener annotation = listener.getClass().getAnnotation(ConsumerListener.class);
            MessageListenerFactory.instance().set(annotation.groupName(),listener);
        }
        // 1. 加载producer
        ProducerFactory.instance().create(mqProperties);
        // 2. 加载consumer
        PushConsumerFactory.instance().create(mqProperties);
        SimpleConsumerFactory.instance().create(mqProperties);
    }

    @PreDestroy
    public void destroy() {
        ProducerFactory.instance().destroy();
        PushConsumerFactory.instance().destroy();
        SimpleConsumerFactory.instance().destroy();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
