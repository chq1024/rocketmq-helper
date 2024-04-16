package com.game.log.engine.base;

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
        private String listener;

        public ConsumerProperties() {
            this.threadNum = 3;
            this.messageCount = 1024;
            this.messageSize = 64 * 1024 * 1024;
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
