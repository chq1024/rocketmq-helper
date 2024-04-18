package com.game.log.engine.mock;

import com.game.log.engine.ab.factory.ProducerFactory;
import com.game.log.engine.conf.LogTableEnum;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
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
        Map<String,Object> content = new HashMap<>();
        content.put("uid",10001);
        content.put("card_id",20003);
        content.put("rate",1);

        Message message = LogTableEnum.GACHA_CARD_LOG.mqMessage(content).transform();
        try {
            Producer producer = ProducerFactory.instance().get(LogTableEnum.GACHA_CARD_LOG.getTopic());
            SendReceipt send = producer.send(message);
            System.out.println("消息:" + send.getMessageId() + "被消费");
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }
}
