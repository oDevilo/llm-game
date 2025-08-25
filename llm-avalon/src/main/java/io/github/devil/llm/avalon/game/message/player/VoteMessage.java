package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public class VoteMessage extends PlayerMessage<VoteMessage.MessageData> {

    public VoteMessage(String gameId, Integer round, Integer turn, MessageData data) {
        super(gameId, round, turn, data);
    }

    @Override
    public String text() {
        return "" + data.agree;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MessageData extends PlayerData {
        /**
         * 赞成
         */
        private boolean agree;
    }

    @Override
    public String type() {
        return Type.VoteMessage;
    }

    @Override
    public MData data() {
        return data;
    }

}