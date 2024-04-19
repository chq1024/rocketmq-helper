package com.game.log.engine.utils;

import com.game.log.engine.ab.factory.ProducerFactory;
import com.game.log.engine.conf.LogTableEnum;
import com.game.log.engine.conf.SendResult;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;

import java.util.Map;

/**
 * 生产者工具类，简化发送消息
 * @author bk
 */
public class ProducerHelper {

    public static SendReceipt send(LogTableEnum tableEnum, Map<String, Object> content) throws ClientException {
        Message message = tableEnum.mqMessage(content).transform();
        Producer producer = ProducerFactory.instance().get(tableEnum.getTopic());
        return producer.send(message);
    }

    public static SendResult sendTransaction(LogTableEnum tableEnum, Map<String, Object> content) throws ClientException {
        Message message = tableEnum.mqMessage(content).transform();
        Producer producer = ProducerFactory.instance().get(tableEnum.getTopic());
        Transaction transaction = producer.beginTransaction();
        SendReceipt send = producer.send(message, transaction);
        SendResult sendResult = new SendResult();
        sendResult.setSendReceipt(send);
        sendResult.setTransaction(transaction);
        return sendResult;
    }

}
