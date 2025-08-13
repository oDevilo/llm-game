package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MissionMessage extends PlayerMessage {

    private boolean success;

    @Override
    public String text() {
        return "" + success;
    }

}
