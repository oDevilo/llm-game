package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public class SpeakMessage extends PlayerMessage<SpeakMessage.MessageData> {

    public SpeakMessage(String gameId, Integer round, Integer turn, MessageData data) {
        super(gameId, round, turn, data);
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
        return Type.SpeakMessage;
    }

    @Override
    public MData data() {
        return data;
    }
}