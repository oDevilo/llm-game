package io.github.devil.llm.avalon.game.runtime.message.player;

import io.github.devil.llm.avalon.game.store.MessageStore;
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

    @Override
    public MessageStore store() {
        MessageStore.VoteMessageStore store = new MessageStore.VoteMessageStore();
        store.setAgree(agree);
        store.setNumber(getNumber());
        return store;
    }

}
