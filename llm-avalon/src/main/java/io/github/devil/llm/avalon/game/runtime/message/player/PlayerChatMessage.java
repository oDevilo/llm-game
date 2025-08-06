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

    private String content;

    @Override
    public String text() {
        return content;
    }

    @Override
    public MessageStore store() {
        MessageStore.PlayerChatMessageStore store = new MessageStore.PlayerChatMessageStore();
        store.setContent(content);
        store.setNumber(getNumber());
        return store;
    }
}
