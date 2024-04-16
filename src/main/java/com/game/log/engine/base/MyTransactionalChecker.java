package com.game.log.engine.base;

import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;

/**
 * @author bk
 */
public class MyTransactionalChecker implements TransactionChecker {

    @Override
    public TransactionResolution check(MessageView messageView) {
        return null;
    }
}
