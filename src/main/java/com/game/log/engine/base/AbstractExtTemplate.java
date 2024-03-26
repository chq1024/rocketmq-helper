package com.game.log.engine.base;


import org.apache.rocketmq.client.apis.ClientException;

/**
 * 对Template进行扩展，方便注入到池子中，扩大生产者和消费者得吞吐量
 * @author bk
 */
public abstract class AbstractExtTemplate {

    public abstract void register() throws ClientException;

    public abstract void release();
}
