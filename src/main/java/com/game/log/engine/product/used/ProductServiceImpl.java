package com.game.log.engine.product.used;

import com.game.log.engine.base.MsgBody;
import com.game.log.engine.base.ProducerTemplate;
import com.game.log.engine.base.TemplateFactory;
import com.game.log.engine.utils.IdHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bk
 */
@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

    @Override
    public void send() {
        ProducerTemplate producer = TemplateFactory.producerTemplate();

        MsgBody msgBody = new MsgBody();
        msgBody.setMsgId(IdHelper.msgId());
        msgBody.setBatchId("-");
        msgBody.setUid(10001L);
        msgBody.setAction("GameSystem");
        Map<String,Object> body = new HashMap<>();
        body.put("before",10);
        body.put("after",11);
        msgBody.setBody(body);

        Message<MsgBody> message = MessageBuilder.withPayload(msgBody).build();
        try {
            SendResult sendResult = producer.syncSend("retry_topic:sync_send", message);
            if (!SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                handlerSendError(producer,message);
            }
        } catch (Exception e) {
            log.error("MQ发送消息失败");
            handlerSendError(producer,message);
        } finally {
            producer.release();
        }
    }

    private void handlerSendError(ProducerTemplate producer,Message<MsgBody> message) {
        // 自动重复发送后依然错误的数据，进入另一个topic去延迟消费消费
        SendResult retrySendResult = producer.syncSendDelayTimeMills("retry_topic", message, 3000);
        if (!SendStatus.SEND_OK.equals(retrySendResult.getSendStatus())) {
            // 丢入到数据库
            System.out.println("丢入数据库~");
        }
    }
}
