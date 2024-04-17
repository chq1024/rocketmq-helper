package com.game.log.engine.conf;

import com.game.log.engine.utils.DateUtil;
import com.game.log.engine.utils.IdHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;

import java.io.Serializable;
import java.util.*;

/**
 * 消息实体
 * @author bk
 */
@Getter
@Setter
@SuperBuilder
public class MqMessage implements Serializable{

    // ----------来自MessageBuilderImpl------------
    private String topic = null;
    private byte[] body = null;
    private String tag = null;
    private String messageGroup = null;
    private Long deliveryTimestamp = null;
    private Collection<String> keys = new HashSet<>();
    private final Map<String, String> properties = new HashMap<>();
    // ----------来自MessageBuilderImpl------------



    private String msgId;
    // 用于对于消息组消息的适配，并更好用于ordered
    private String batchId;
    private String action;
    private Long uid;
    private Map<String, Object> content;
    private Long createTime;
    // 用于排序,顺序消息对于此类消费者不太适用，下游要使用多线程消费无法保证消费消息的顺序性，所以通过程序排序
    private Long ordered;
    // 消息重试消费次数，如果超过了定义的次数，可持久化到数据库
    private Integer retry;

    public MqMessage() {
        this(1L,0);
    }

    public MqMessage(Long ordered,Integer retry) {
        this.msgId = IdHelper.msgId();
        this.createTime = DateUtil.timestamp();
        this.ordered = ordered;
        this.retry = retry;
    }
}
