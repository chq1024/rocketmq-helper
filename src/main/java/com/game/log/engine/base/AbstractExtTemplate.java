package com.game.log.engine.base;

import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * 对Template进行扩展，方便注入到池子中，扩大生产者和消费者得吞吐量
 * @author bk
 */
public abstract class AbstractExtTemplate extends RocketMQTemplate {

    public abstract void register();

    public abstract void release();
}
