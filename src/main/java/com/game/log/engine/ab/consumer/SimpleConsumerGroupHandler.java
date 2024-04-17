package com.game.log.engine.ab.consumer;

import com.game.log.engine.ab.MessageHandler;
import com.game.log.engine.anno.ConsumerHandler;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * @author bk
 */
@ConsumerHandler(groupName = "simple_consumers_group")
public class SimpleConsumerGroupHandler implements MessageHandler {

    @Override
    public void handler(MessageView messageView) {

    }
}
