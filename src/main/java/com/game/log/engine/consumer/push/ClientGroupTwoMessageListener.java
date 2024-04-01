package com.game.log.engine.consumer.push;

import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;

/**
 * @author bk
 */
public class ClientGroupTwoMessageListener extends AbstractListener {

    @Override
    public ConsumeResult consume(MessageView messageView) {
        System.out.println(messageView);
        return ConsumeResult.SUCCESS;
    }
}
