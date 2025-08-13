package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerChatMessage extends PlayerMessage {

    private String thinking;

    private String speak;

    @Override
    public String text() {
        return speak;
    }
    
}