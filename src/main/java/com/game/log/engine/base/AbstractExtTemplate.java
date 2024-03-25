package com.game.log.engine.base;

import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * @author bk
 */
public abstract class AbstractExtTemplate extends RocketMQTemplate {

    public abstract void register();

    public abstract void release();
}
