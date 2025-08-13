package io.github.devil.llm.avalon.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PlayerRole {

    UNKNOWN(CommonConstants.UNKNOWN, CampType.UNKNOWN, "未知"),
    MERLIN("Merlin", CampType.BLUE, "梅林"),
    PERCIVAL("Percival", CampType.BLUE, "派西维尔"),
    Loyal_Minister("Loyal Minister", CampType.BLUE, "忠臣"),
    MORGANA("Morgana", CampType.RED, "莫甘娜"),
    ASSASSIN("Assassin", CampType.RED, "刺客"),
    MORDRED("Mordred", CampType.RED, "莫德雷德"),
    OBERON("Oberon", CampType.RED, "奥伯伦"),
    CLAWS("Claws", CampType.RED, "爪牙"),
    ;

    @JsonValue
    public final String value;

    public final CampType camp;

    public final String desc;

    PlayerRole(String value, CampType camp, String desc) {
        this.value = value;
        this.camp = camp;
        this.desc = desc;
    }

    /**
     * 校验是否是当前状态
     *
     * @param type 待校验值
     */
    public boolean is(PlayerRole type) {
        return equals(type);
    }

    /**
     * 校验是否是当前状态
     *
     * @param value 待校验状态值
     */
    public boolean is(String value) {
        return this.value.equals(value);
    }

    /**
     * 解析上下文状态值
     */
    @JsonCreator
    public static PlayerRole parse(String value) {
        for (PlayerRole v : values()) {
            if (v.is(value)) {
                return v;
            }
        }
        return UNKNOWN;
    }
}