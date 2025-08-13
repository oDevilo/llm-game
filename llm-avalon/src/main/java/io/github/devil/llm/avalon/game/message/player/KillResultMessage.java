package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KillResultMessage extends PlayerMessage {

    private int killNumber;

    @Override
    public String text() {
        return "" + killNumber;
    }

}