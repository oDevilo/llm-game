package io.github.devil.llm.avalon.game.message;

/**
 * @author Devil
 */
public interface Message {

    String text();

    Source source();

    enum Source {
        HOST, // 主持人
        PLAYER, // 玩家
        ;
    }
}