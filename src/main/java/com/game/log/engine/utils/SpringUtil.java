package com.game.log.engine.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * spring工具类
 * - 主要用于获取容器bean
 * @author chuhq
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    public static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(String name,Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}
