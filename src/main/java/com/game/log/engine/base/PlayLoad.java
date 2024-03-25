package com.game.log.engine.base;

import com.game.log.engine.utils.IdHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

/**
 * 消息实体
 *
 * @author bk
 */
@Getter
@Setter
public class PlayLoad implements Serializable {
    private String msgId;
    // 用于对于消息组消息的适配，并更好用于ordered
    private String batchId;
    private String action;
    private Long uid;
    private Map<String, Object> body;
    private Long createTime;
    // 用于排序,顺序消息对于此类消费者不太适用，下游要使用多线程消费无法保证消费消息的顺序性，所以通过程序排序
    private Long ordered;

    public PlayLoad() {
        this(1L);
    }

    public PlayLoad(Long ordered) {
        this.msgId = IdHelper.msgId();
        this.createTime = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        this.ordered = ordered;
    }
}
