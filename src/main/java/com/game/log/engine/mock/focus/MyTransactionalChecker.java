package com.game.log.engine.mock.focus;

import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.producer.TransactionChecker;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;

/**
 * 一个生产者可以处理多个主题，对于这种情况要复用同一个checker，需要通过messageView进行合理的区分
 * @author bk
 */
public class MyTransactionalChecker implements TransactionChecker {

    @Override
    public TransactionResolution check(MessageView messageView) {
        System.out.println("事务消息:" + messageView.getMessageId());
        return TransactionResolution.COMMIT;
    }
}
