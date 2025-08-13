package io.github.devil.llm.avalon.game.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Devil
 */
@Setter
@Getter
public abstract class PlayerMessage implements Message {
    /**
     * 玩家号码
     */
    private int number;

    @Override
    public Source source() {
        return Source.PLAYER;
    }

}