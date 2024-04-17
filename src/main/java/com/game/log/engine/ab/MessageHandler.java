package com.game.log.engine.ab;

import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * 消息处理类接口
 * @author bk
 */
public interface MessageHandler {
    void handler(MessageView messageView);
}
