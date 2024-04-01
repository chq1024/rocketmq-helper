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
        private Integer initNum;
        private Integer maxAttempts;
        private List<String> topics;

        public ProducerProperties() {
            this.namespace = "DEFAULT-NAME-SPACE";
            this.initNum = 1;
            this.maxAttempts = 3;
        }
    }

    @Data
    public static class ConsumerProperties {
        private String type;
        private List<String> topicTags;
        private Integer initNum;
        private Integer threadNum;
        private Integer messageCount;
        private Integer messageSize;
        private String listener;

        public ConsumerProperties() {
            this.threadNum = 3;
            this.initNum = 1;
            this.messageCount = 1024;
            this.messageSize = 64 * 1024 * 1024;
        }
    }
}
