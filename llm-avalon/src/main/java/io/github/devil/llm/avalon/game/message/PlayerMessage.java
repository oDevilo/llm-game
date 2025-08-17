package io.github.devil.llm.avalon.game.message;

import lombok.Data;
import lombok.Getter;

/**
 * @author Devil
 */
@Getter
public abstract class PlayerMessage<T extends PlayerMessage.PlayerData> extends Message {

    protected final T data;

    public PlayerMessage(String gameId, T data) {
        super(gameId);
        this.data = data;
    }

    @Override
    public Source source() {
        return Source.PLAYER;
    }

    @Data
    public static class PlayerData implements MData {
        /**
         * 玩家号码
         */
        private int number;
    }

}