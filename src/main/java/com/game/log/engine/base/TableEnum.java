package com.game.log.engine.base;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TableEnum {

    GAME_SYSTEM_LOG("GameSystem","game_system_tab"),

    GACHA_CARD_LOG("GachaCard","gacha_card_tab");

    private String action;
    private String tableName;
    TableEnum(String action,String tableName) {
        this.action = action;
        this.tableName = tableName;
    }

    public static TableEnum valueOfAction(String action) {
        return Arrays.stream(values()).filter(r->r.getAction().equals(action)).findAny().orElseThrow(()->new RuntimeException("未发现对应的表枚举"));
    }
}
