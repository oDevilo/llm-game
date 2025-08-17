package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public class DraftTeamMessage extends PlayerMessage<DraftTeamMessage.MessageData> {

    public DraftTeamMessage(String gamId, MessageData data) {
        super(gamId, data);
    }

    @Override
    public String text() {
        return data.content;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MessageData extends PlayerData {

        /**
         * 主要内容
         */
        private String content;
        /**
         * 确定的发言顺序
         */
        private String speakOrder;
    }

    @Override
    public String type() {
        return Type.DraftTeamMessage;
    }

    @Override
    public MData data() {
        return data;
    }

}