package io.github.devil.llm.avalon.game.message;

import lombok.Data;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public abstract class PlayerMessage<T extends PlayerMessage.PlayerData> implements Message {

    protected final T data;

    public PlayerMessage(T data) {
        this.data = data;
    }

    @Override
    public Source source() {
        return Source.PLAYER;
    }

    @Data
    public static class PlayerData {
        /**
         * 玩家号码
         */
        private int number;
    }

}