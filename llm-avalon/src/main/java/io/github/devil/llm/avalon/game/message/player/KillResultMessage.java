package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public class KillResultMessage extends PlayerMessage<KillResultMessage.MessageData> {

    public KillResultMessage(String gamId, MessageData data) {
        super(gamId, data);
    }

    @Override
    public String text() {
        return "" + data.killNumber;
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MessageData extends PlayerData {

        private int killNumber;
    }

    @Override
    public String type() {
        return Type.KillResultMessage;
    }

    @Override
    public MData data() {
        return data;
    }
}