package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public class PlayerChatMessage extends PlayerMessage<PlayerChatMessage.MessageData> {

    public PlayerChatMessage(String gamId, MessageData data) {
        super(gamId, data);
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
        return Type.PlayerChatMessage;
    }

    @Override
    public MData data() {
        return data;
    }
}