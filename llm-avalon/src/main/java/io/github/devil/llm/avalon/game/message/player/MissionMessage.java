package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public class MissionMessage extends PlayerMessage<MissionMessage.MessageData> {

    public MissionMessage(String gamId, MessageData data) {
        super(gamId, data);
    }

    @Override
    public String text() {
        return "" + data.success;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MessageData extends PlayerData {

        private boolean success;
    }

    @Override
    public String type() {
        return Type.MissionMessage;
    }

    @Override
    public MData data() {
        return data;
    }

}
