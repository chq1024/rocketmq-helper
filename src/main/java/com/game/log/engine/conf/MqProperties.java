package com.game.log.engine.conf;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;


/**
 * mq需要的参数
 * @author bk
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "mq")
public class MqProperties {

    private String proxy;
    private Boolean enableSsl;
    private ProducerProperties producer;
    private Map<String,ConsumerProperties> consumers;

    public MqProperties() {
        this.enableSsl = false;
    }


    @Data
    public static class ProducerProperties {
        private String namespace;
        private Integer maxAttempts;
        private Map<String,TopicProperties> topics;
        private String checker;

        public ProducerProperties() {
            this.namespace = "DEFAULT-NAME-SPACE";
            this.maxAttempts = 3;
        }
    }

    @Data
    public static class ConsumerProperties {
        private String type;
        private List<String> topicTags;
        private Integer threadNum;
        private Integer messageCount;
        private Integer messageSize;
        private Integer awaitSeconds;
        private Integer receiveNum;
        private Integer invisibleSeconds;

        public ConsumerProperties() {
            this.threadNum = 3;
            this.messageCount = 1024;
            this.messageSize = 64 * 1024 * 1024;
            this.awaitSeconds = 5;
            //消费不可见时间指的是消息处理+失败后重试间隔的总时长
            this.invisibleSeconds = 10;
        }
    }

    @Data
    public static class TopicProperties {
        private Boolean isTrans;

        public TopicProperties() {
            this.isTrans = false;
        }
    }
}
