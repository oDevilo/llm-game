package io.github.devil.llm.avalon.game.runtime.message;

import io.github.devil.llm.avalon.game.store.MessageStore;

/**
 * @author Devil
 */
public interface Message {

    String text();

    Source source();

    MessageStore store();

    enum Source {
        HOST, // 主持人
        PLAYER, // 玩家
        ;
    }
}
