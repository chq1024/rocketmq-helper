package com.game.log.engine.conf;

import com.game.log.engine.utils.DateUtil;
import com.game.log.engine.utils.IdHelper;
import com.game.log.engine.utils.TransformUtil;
import lombok.Builder;
import lombok.Data;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * 消息实体
 * @author bk
 */
@Data
@Builder
public class MqMessage implements Serializable{

    // ----------来自MessageBuilderImpl------------
    private String topic;
    private String tag;
    private String messageGroup;
    private Long deliveryTimestamp;
    private HashSet<String> keys;
    private Map<String, String> properties;
    // ----------来自MessageBuilderImpl------------


    // 内部消息标识，避免重复插入
    private String msgId;
    // 表名,用一个表中的数据可放置同一个messageGroup中，在pushConsumer中可保持消费顺序，在simpleConsumer中需要借助od字段保持循序
    private String tb;
    // 自定义内容，内容会被下游插入表时被肢解成字段名
    private Map<String, Object> content;
    private Long createTime;
    // od->order 使用第三方插件自增 redis.incr  命名规则 240101+00000000 每日清空计数
    private Long od;
    // 重试次数，计数用于自定义处理规则或监控计数，理论上业务出现的异常消息不应该再被重试，可以丢弃；mq调控出现的异常可以重试，但最终都需要db保持消息唯一性
    private Integer retry;

    public Message transform() {
        MessageBuilder messageBuilder = new MessageBuilderImpl();
        if (!StringUtils.hasText(this.topic)) {
            throw new RuntimeException("消息体topic必填");
        }
        if (!StringUtils.hasText(this.tb)) {
            throw new RuntimeException("消息体tb必填");
        }
        messageBuilder.setTopic(this.topic);
        if (StringUtils.hasText(this.tag)) {
            messageBuilder.setTag(this.tag);
        }
        if (StringUtils.hasText(this.messageGroup)) {
            messageBuilder.setMessageGroup(this.messageGroup);
        }
        if (this.deliveryTimestamp != null) {
            messageBuilder.setDeliveryTimestamp(this.deliveryTimestamp);
        }
        if (this.keys != null && !this.keys.isEmpty()) {
            messageBuilder.setKeys(this.keys.toArray(new String[0]));
        }
        if (this.properties != null && !this.properties.isEmpty()) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                messageBuilder.addProperty(entry.getKey(),entry.getValue());
            }
        }
        Map<String,Object> bodyMap = new HashMap<>();
        Long msgID = IdHelper.msgId();
        bodyMap.put("msgId", String.valueOf(msgID));
        bodyMap.put("tb",this.tb);
        bodyMap.put("content", Optional.ofNullable(this.content).orElse(new HashMap<>()));
        bodyMap.put("createTime", DateUtil.timestamp());
        bodyMap.put("od",msgID);
        bodyMap.put("retry",Optional.ofNullable(this.retry).orElse(0));
        messageBuilder.setBody(TransformUtil.toByteArray(bodyMap));

        return messageBuilder.build();
    }
}
