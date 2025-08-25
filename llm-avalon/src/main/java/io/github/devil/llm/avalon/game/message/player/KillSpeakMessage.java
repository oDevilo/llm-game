package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public class KillSpeakMessage extends PlayerMessage<KillSpeakMessage.MessageData> {

    public KillSpeakMessage(String gameId, MessageData data) {
        super(gameId, data);
    }

    @Override
    public String text() {
        return data.speak;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MessageData extends PlayerData {

        private String thinking;

        private String speak;
    }

    @Override
    public String type() {
        return Type.KillSpeakMessage;
    }

    @Override
    public MData data() {
        return data;
    }
}