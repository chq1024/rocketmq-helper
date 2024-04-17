package com.game.log.engine.mock;

import com.game.log.engine.ab.factory.ProducerFactory;
import com.game.log.engine.conf.MqMessage;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.stereotype.Service;

/**
 * @author bk
 */
@Service
public class ProductServiceImpl implements IProductService {


    @Override
    public void send() {


        MqMessage.builder()

        try {
            Producer producer = ProducerFactory.instance().get("send");
            producer.send()
            SendReceipt send = producer.send(messageBuilder.build());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
//        SendResult sendResult = producer.syncSend("comment_topic:sync_send", message);
//
//        if (!SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
//            System.out.println("消息发送失败！");
//            // 自动重复发送后依然错误的数据，进入另一个topic去延迟消费消费
//            SendResult retrySendResult = producer.syncSendDelayTimeMills("retry_topic", message, 3000);
//            if (!SendStatus.SEND_OK.equals(retrySendResult.getSendStatus())) {
//                // 丢入到数据库
//                System.out.println("丢入数据库~");
//            }
//        }
    }
}
