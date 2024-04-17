package com.game.log.engine.anno;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 消费者handler
 * @author bk
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ConsumerHandler {

    String groupName() default "";
}
