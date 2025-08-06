package io.github.devil.llm.avalon.game.runtime.message.player;

import io.github.devil.llm.avalon.game.store.MessageStore;
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

    @Override
    public MessageStore store() {
        MessageStore.PlayerChatMessageStore store = new MessageStore.PlayerChatMessageStore();
        store.setSpeak(speak);
        store.setThinking(thinking);
        store.setNumber(getNumber());
        return store;
    }
}
