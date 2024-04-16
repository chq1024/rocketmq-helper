package com.game.log.engine.product;

import com.game.log.engine.base.Message;
import com.game.log.engine.factory.ProducerFactory;
import com.game.log.engine.utils.IdHelper;
import com.game.log.engine.utils.TransformUtil;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bk
 */
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProducerFactory factory;

    @Override
    public void send() {
        Producer producer = factory.get();

        Message msgBody = new Message();
        msgBody.setMsgId(IdHelper.msgId());
        msgBody.setBatchId("-");
        msgBody.setUid(10001L);
        msgBody.setAction("GameSystem");
        Map<String,Object> body = new HashMap<>();
        body.put("before",10);
        body.put("after",11);
        msgBody.setBody(body);

        MessageBuilder messageBuilder = new MessageBuilderImpl();

        messageBuilder.setBody(TransformUtil.toByteArray(msgBody));
        messageBuilder.setKeys("");

        messageBuilder.setTag("");
        messageBuilder.setTopic("");

        try {
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
        factory.release(producer);
    }
}
