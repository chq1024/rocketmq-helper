package com.game.log.engine.product.used;

import com.game.log.engine.base.PlayLoad;
import com.game.log.engine.product.ProductFactory;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author bk
 */
@Service
public class ProductServiceImpl implements IProductService {

    @Override
    public void send(Map<String,Object> content) {
        RocketMQTemplate template = ProductFactory.commentTemplate();

        PlayLoad playLoad = new PlayLoad();
        playLoad.setAction("cmd");
        playLoad.setUid(10001L);
        playLoad.setBody(content);
        Message<PlayLoad> message = MessageBuilder.withPayload(playLoad).build();
        SendResult sendResult = template.syncSend("topic_A:tagA:tagB", message);
        System.out.println("send status:" + sendResult.getSendStatus().name());
    }
}
