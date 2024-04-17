package com.game.log.engine.ab.factory;

import com.game.log.engine.ab.AbMessageFactory;
import org.apache.rocketmq.client.apis.consumer.MessageListener;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息监听者工厂
 * @author bk
 */
public class MessageListenerFactory extends AbMessageFactory<MessageListener> {

    public ConcurrentHashMap<String, MessageListener> groupMap = new ConcurrentHashMap<>();

    private volatile static MessageListenerFactory factory;

    private MessageListenerFactory() {}

    public static MessageListenerFactory instance(){
        if (null == factory){
            synchronized (MessageListenerFactory.class){
                if (null == factory){
                    factory = new MessageListenerFactory();
                }
            }
        }
        return factory;
    }

    @Override
    public MessageListener get(String groupName) {
        boolean contained = groupMap.containsKey(groupName);
        if (!contained) {
            throw new RuntimeException(String.format("未发现消费者组%s的消息监听者",groupName));
        }
        return groupMap.get(groupName);
    }

    @Override
    public void set(String groupName, MessageListener listener) {
        groupMap.put(groupName,listener);
    }
}
