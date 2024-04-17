package com.game.log.engine.ab.consumer;

import com.game.log.engine.anno.ConsumerListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * @author bk
 */
@ConsumerListener(groupName = "push_consumer_group")
public class PushConsumerGroupListener implements MessageListener {

    @Override
    public ConsumeResult consume(MessageView messageView) {
        System.out.println(messageView);
        return ConsumeResult.SUCCESS;
    }
}
