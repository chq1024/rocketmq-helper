package com.game.log.engine.utils;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author bk
 */
public class IdHelper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");

    private static RedisTemplate<String,Object> redisTemplate;

    @SuppressWarnings("unchecked")
    private static RedisTemplate<String,Object> redisTemplate() {
        if (redisTemplate == null) {
            synchronized (IdHelper.class) {
                if (redisTemplate == null) {
                    redisTemplate = (RedisTemplate<String,Object>) SpringUtil.getBean("redisTemplate",RedisTemplate.class);
                }
            }
        }
        return redisTemplate;
    }

    public static Long msgId() {
        String yyMmDd = LocalDate.now(ZoneId.of("GMT+8")).format(formatter);
        String key = "game:log:msg:" + yyMmDd;
        if (Boolean.FALSE.equals(redisTemplate().hasKey(key))) {
            redisTemplate().opsForValue().setIfAbsent(key,0,1, TimeUnit.DAYS);
        }
        Long currAtomic = redisTemplate().opsForValue().increment(key);
        return Long.parseLong(yyMmDd + currAtomic);
    }
}
