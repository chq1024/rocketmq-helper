package com.game.log.engine.conf;

import lombok.Getter;
import lombok.Setter;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;

/**
 * @author bk
 */
@Getter
@Setter
public class SendResult {

    private SendReceipt sendReceipt;
    private Transaction transaction;
}
