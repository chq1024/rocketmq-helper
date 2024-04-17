package com.game.log.engine.ab;

import com.game.log.engine.conf.MqProperties;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bk
 */
public abstract class AbFactory<T> {

    public abstract void create(MqProperties properties) throws ClientException;

    public abstract void destroy();

    public abstract T get(String keywords);

    public Map<String, FilterExpression> parseExpressions(List<String> expressions) {
        Map<String, FilterExpression> relationMap = new HashMap<>(expressions.size());
        for (String expression : expressions) {
            String[] arr = expression.split(":");
            String topic = arr[0];
            List<String> tags = Arrays.asList(arr).subList(1, arr.length);
            if (relationMap.containsKey(topic)) {
                throw new RuntimeException("consumer.topic-tags配置有误,同一个消费者中不能多次出现相同的topic");
            }
            relationMap.put(topic, new FilterExpression(String.join("|", tags)));
        }
        return relationMap;
    }

}
