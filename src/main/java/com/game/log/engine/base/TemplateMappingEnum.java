package com.game.log.engine.base;

import lombok.Getter;

/**
 * 消息类型枚举
 * @author bk
 */
@Getter
public enum TemplateMappingEnum {

    /**
     * 生产者
     */
    PRODUCT_TEMPLATE("product"),

    /**
     * 普通消息
     */
    MSG_COMMENT_MQ("comment"),

    /**
     * 事务消息
     */
    MSG_TRANSACTION_MQ("transactional"),
    ;

    private final String type;

    TemplateMappingEnum(String type) {
        this.type = type;
    }
}
