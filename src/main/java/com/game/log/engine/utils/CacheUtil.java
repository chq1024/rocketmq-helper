package com.game.log.engine.utils;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于维护当前项目中需要的一些缓存
 * @author bk
 */
public class CacheUtil {

    // key:消息主题 value:消息类型
    public static Map<String,String> tmtCache = new HashMap<>();

    public static void setTmtCache(String key,String value) {
        tmtCache.put(key,value);
    }

    public static String getTmtCache(String topic) {
        String mt = tmtCache.get(topic);
        if (!StringUtils.hasText(mt)) {
            throw new RuntimeException("未发现topic为"+ topic + "的消息类型，请检查配置");
        }
        return mt;
    }

}
