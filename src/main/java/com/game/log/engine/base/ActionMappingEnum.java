package com.game.log.engine.base;

import lombok.Getter;

import java.util.Arrays;

/**
 * 配置action与表的映射关系
 * @author beikei
 */
@Getter
public enum ActionMappingEnum {

    GAME_SYSTEM_LOG("GameSystem","game_system_tab"),
    GACHA_CARD_LOG("GachaCard","gacha_card_tab"),
    ;

    private final String action;
    private final String tableName;

    ActionMappingEnum(String action,String tableName) {
        this.action = action;
        this.tableName = tableName;
    }

    public static ActionMappingEnum valueOfAction(String action) {
        return Arrays.stream(values()).filter(r->r.getAction().equals(action)).findAny().orElseThrow(()->new RuntimeException("未发现对应的表枚举"));
    }
}
