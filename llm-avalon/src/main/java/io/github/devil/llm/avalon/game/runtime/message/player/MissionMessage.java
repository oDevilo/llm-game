package io.github.devil.llm.avalon.game.runtime.message.player;

import io.github.devil.llm.avalon.game.store.MessageStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MissionMessage extends PlayerMessage {

    private boolean success;

    @Override
    public String text() {
        return "" + success;
    }

    @Override
    public MessageStore store() {
        MessageStore.MissionMessageStore store = new MessageStore.MissionMessageStore();
        store.setSuccess(success);
        store.setNumber(getNumber());
        return store;
    }

}
