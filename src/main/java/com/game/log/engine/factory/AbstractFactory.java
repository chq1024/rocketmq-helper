package com.game.log.engine.factory;

import org.apache.rocketmq.client.apis.consumer.FilterExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bk
 */
public abstract class AbstractFactory {

    public abstract void destroy();

    public Map<String, FilterExpression> parseExpressions(List<String> expressions) {
        Map<String, FilterExpression> res = new HashMap<>(expressions.size());
        for (String expression : expressions) {
            String[] arr = expression.split(":");
            String topic = arr[0];
            List<String> tags = new ArrayList<>(arr.length - 1);
            for (int i = 1; i < arr.length; i++) {
                tags.add(arr[i]);
            }
            if (res.containsKey(topic)) {
                throw new RuntimeException("配置有误,同一个消费者中不能出现相同的topic");
            }
            res.put(topic, new FilterExpression(String.join("|", tags)));
        }
        return res;
    }

}
