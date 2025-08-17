package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

/**
 * @author Devil
 */
@Getter
public class ConfirmTeamMessage extends PlayerMessage<ConfirmTeamMessage.MessageData> {

    public ConfirmTeamMessage(String gamId, MessageData data) {
        super(gamId, data);
    }

    @Override
    public String text() {
        return data.content;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MessageData extends PlayerData {

        private String content;

        private Set<Integer> teamNumbers;
    }

    @Override
    public String type() {
        return Type.ConfirmTeamMessage;
    }

    @Override
    public MData data() {
        return data;
    }
}