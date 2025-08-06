package io.github.devil.llm.avalon.game.runtime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.devil.llm.avalon.constants.CampType;
import io.github.devil.llm.avalon.constants.CommonConstants;
import io.github.devil.llm.avalon.constants.SpeakOrder;
import io.github.devil.llm.avalon.game.runtime.message.Message;
import io.github.devil.llm.avalon.game.runtime.message.player.PlayerMessage;
import lombok.Getter;

import java.util.List;
import java.util.Set;

/**
 * @author Devil
 */
@Getter
public abstract class Player {

    protected String gameId;
    /**
     * 号码牌
     */
    protected int number;
    /**
     * 扮演角色
     */
    protected Role role;

    protected MessageHistory messageHistory;

    public Player(String gameId, int number, Role role, MessageHistory messageHistory) {
        this.gameId = gameId;
        this.number = number;
        this.role = role;
        this.messageHistory = messageHistory;
    }

    public abstract void init(List<Player> players);

    /**
     * 拟定队伍
     * @return 发言顺序 true：顺时针 false：逆时针
     */
    public abstract SpeakOrder draftTeam();

    /**
     * 发言
     */
    public abstract String chat();

    /**
     * 选举的进行任务的人员号码
     */
    public abstract Set<Integer> team();

    /**
     * 投票出任务
     */
    public abstract boolean vote();

    /**
     * 任务成功失败
     */
    public abstract boolean mission();

    /**
     * 刺杀
     */
    public abstract int kill();

    protected void addMessage(PlayerMessage message) {
        message.setNumber(number);
        messageHistory.add(message);
    }

    public enum Role {

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

        Role(String value, CampType camp, String desc) {
            this.value = value;
            this.camp = camp;
            this.desc = desc;
        }

        /**
         * 校验是否是当前状态
         *
         * @param type 待校验值
         */
        public boolean is(Role type) {
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
        public static Role parse(String value) {
            for (Role v : values()) {
                if (v.is(value)) {
                    return v;
                }
            }
            return UNKNOWN;
        }
    }

}
