package com.game.log.engine.consumer.templates;

import com.game.log.engine.base.AbstractExtTemplate;
import org.apache.rocketmq.spring.annotation.ExtRocketMQConsumerConfiguration;

import javax.annotation.PostConstruct;

/**
 * @author bk
 */
@ExtRocketMQConsumerConfiguration(nameServer = "${rocketmq.name-server}",value = "commentOneConsumer", instanceName = "",group = "${mq.consumer.comment.group}")
public class CommentMsgOneConsumer extends AbstractExtTemplate {

    @PostConstruct
    public void inited() {
        register();
    }


    @Override
    public void register() {

    }

    @Override
    public void release() {

    }
}
