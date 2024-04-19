package com.game.log.engine.mock;

import com.game.log.engine.ab.factory.ProducerFactory;
import com.game.log.engine.conf.LogTableEnum;
import com.game.log.engine.conf.SendResult;
import com.game.log.engine.utils.ProducerHelper;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bk
 */
@Service
public class ProductServiceImpl implements IProductService {

    @Override
    public void send() {
        Map<String,Object> content1 = new HashMap<>();
        content1.put("uid",10001);
        content1.put("card_id",20003);
        content1.put("rate",1);

        try {
            SendResult sendResult = ProducerHelper.sendTransaction(LogTableEnum.GACHA_CARD_LOG, content1);
            Transaction transaction = sendResult.getTransaction();
            SendReceipt sendReceipt = sendResult.getSendReceipt();
            transaction.commit();
            System.out.println("消息:" + sendReceipt.getMessageId() + "被消费");
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
        Map<String,Object> content2 = new HashMap<>();
        content2.put("uid",10001);
        content2.put("card_id",20003);
        content2.put("rate",1);

        try {
            SendReceipt send = ProducerHelper.send(LogTableEnum.USE_ITEM_LOG, content2);
            System.out.println("消息:" + send.getMessageId() + "被消费");
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }
}
