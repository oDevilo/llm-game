package io.github.devil.llm.avalon.game.runtime.message.player;

import io.github.devil.llm.avalon.game.store.MessageStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KillResultMessage extends PlayerMessage {

    private int killNumber;

    @Override
    public String text() {
        return "" + killNumber;
    }

    @Override
    public MessageStore store() {
        MessageStore.KillResultMessageStore store = new MessageStore.KillResultMessageStore();
        store.setKillNumber(killNumber);
        store.setNumber(getNumber());
        return store;
    }

}
