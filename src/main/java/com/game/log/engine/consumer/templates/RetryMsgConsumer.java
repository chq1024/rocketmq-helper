package com.game.log.engine.consumer.templates;

import com.game.log.engine.base.MsgBody;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @author bk
 */
@Service
@RocketMQMessageListener(topic = "${mq.topic.retry}",consumerGroup = "${mq.consumer.retry.group}",selectorExpression = "*",tlsEnable = "false",
        delayLevelWhenNextConsume = 1,
        maxReconsumeTimes = 3,
        consumeThreadNumber = 2,
        consumeThreadMax = 6)
public class RetryMsgConsumer implements RocketMQListener<MsgBody> {

    @Override
    public void onMessage(MsgBody message) {
        // 处理发送重试后失败的消息
        throw new RuntimeException("消费错误");
    }
}
