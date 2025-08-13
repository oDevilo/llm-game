package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VoteMessage extends PlayerMessage {
    /**
     * 赞成
     */
    private boolean agree;

    @Override
    public String text() {
        return "" + agree;
    }

}