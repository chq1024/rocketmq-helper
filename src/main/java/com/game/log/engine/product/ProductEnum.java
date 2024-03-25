package com.game.log.engine.product;

import lombok.Getter;

/**
 * @author bk
 */
@Getter
public enum ProductEnum {

    COMMENT_MQ("comment"),

    SPECIAL_MQ("special");

    private final String type;

    ProductEnum(String type) {
        this.type = type;
    }
}
