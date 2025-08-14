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

    public VoteMessage(MessageData data) {
        super(data);
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

}