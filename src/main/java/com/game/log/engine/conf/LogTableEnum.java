package com.game.log.engine.conf;

import lombok.Getter;

import java.util.Map;

/**
 * 表格映射enum
 * @author bk
 */
@Getter
public enum LogTableEnum {

    GACHA_CARD_LOG(1,"log_comment_topic","log_gacha_card_tb"),
    USE_ITEM_LOG(2,"log_comment_topic","log_use_item_tb"),
    ;
    private int idx;
    private String topic;
    private String tbName;

    LogTableEnum(int idx,String topic,String tbName) {
        this.idx = idx;
        this.topic = topic;
        this.tbName = tbName;
    }


    public String messageGroup() {
        int idx = this.getIdx();
        int remi = idx % 3;
        if (remi == 1) {
            return "message_group_1";
        } else if (remi == 2) {
            return "message_group_2";
        } else {
            return "message_group_0";
        }
    }

    public MqMessage mqMessage(Map<String,Object> content) {
        return MqMessage.builder()
                .topic(this.getTopic())
                .tb(this.getTbName())
                .messageGroup(this.messageGroup())
                .content(content)
                .build();
    }
}
