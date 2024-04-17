package com.game.log.engine.ab.factory;

import com.game.log.engine.ab.AbMessageFactory;
import com.game.log.engine.ab.MessageHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息处理类工厂
 *
 * @author bk
 */
public class MessageHandlerFactory extends AbMessageFactory<MessageHandler> {

    public ConcurrentHashMap<String, MessageHandler> groupMap = new ConcurrentHashMap<>();

    private volatile static MessageHandlerFactory factory;

    public static MessageHandlerFactory instance() {
        if (null == factory) {
            synchronized (MessageHandlerFactory.class) {
                if (null == factory) {
                    factory = new MessageHandlerFactory();
                }
            }
        }
        return factory;
    }

    @Override
    public MessageHandler get(String groupName) {
        boolean contained = groupMap.containsKey(groupName);
        if (!contained) {
            throw new RuntimeException(String.format("未发现消费者组%s的消息监听者", groupName));
        }
        return groupMap.get(groupName);
    }

    @Override
    public void set(String groupName, MessageHandler messageHandler) {
        groupMap.put(groupName, messageHandler);
    }
}
