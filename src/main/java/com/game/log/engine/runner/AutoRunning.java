package com.game.log.engine.runner;

import com.game.log.engine.ab.MessageHandler;
import com.game.log.engine.ab.factory.*;
import com.game.log.engine.anno.ConsumerHandler;
import com.game.log.engine.anno.ConsumerListener;
import com.game.log.engine.conf.MqProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.filter.impl.Op;
import org.apache.rocketmq.srvutil.ServerUtil;
import org.apache.rocketmq.tools.admin.MQAdminUtils;
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
import java.util.Set;

/**
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
        // 0. 手动创建主题
        Map<String, MqProperties.TopicProperties> topics = mqProperties.getProducer().getTopics();
        System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, "192.168.5.182:9876");
        Set<String> topicsNames = topics.keySet();
        for (String topicsName : topicsNames) {
            UpdateTopicSubCommand command = new UpdateTopicSubCommand();
            String[] params = new String[] {
                    "-b 192.168.5.182:10911",
                    "-t " + topicsName,
                    "-o " + true,
                    "-n 192.168.5.182:9876",
                    "-r 3",
                    "-w 3",
                    "-p 6",
                    "-a +message.type=FIFO",
            };
            Options options = ServerUtil.buildCommandlineOptions(new Options());
            final Options updateTopicOptions = command.buildCommandlineOptions(options);
            CommandLine mqadmin = ServerUtil.parseCmdLine("mqadmin", params, updateTopicOptions, new PosixParser());
            try {
                command.execute(mqadmin,updateTopicOptions,null);
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
