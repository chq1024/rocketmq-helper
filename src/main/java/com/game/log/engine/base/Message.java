package com.game.log.engine.base;

import com.game.log.engine.utils.DateUtil;
import com.game.log.engine.utils.IdHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息实体
 * @author bk
 */
@Getter
@Setter
public class Message implements Serializable {

    private String msgId;
    // 用于对于消息组消息的适配，并更好用于ordered
    private String batchId;
    private String action;
    private Long uid;
    private Map<String, Object> body;
    private Long createTime;
    // 用于排序,顺序消息对于此类消费者不太适用，下游要使用多线程消费无法保证消费消息的顺序性，所以通过程序排序
    private Long ordered;
    // 消息重试消费次数，如果超过了定义的次数，可持久化到数据库
    private Integer retry;

    public Message() {
        this(1L,0);
    }

    public Message(Long ordered,Integer retry) {
        this.msgId = IdHelper.msgId();
        this.createTime = DateUtil.timestamp();
        this.ordered = ordered;
        this.retry = retry;
    }
}
