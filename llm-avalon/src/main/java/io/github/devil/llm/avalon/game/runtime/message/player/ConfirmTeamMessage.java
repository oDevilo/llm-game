package io.github.devil.llm.avalon.game.runtime.message.player;

import io.github.devil.llm.avalon.game.store.MessageStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConfirmTeamMessage extends PlayerMessage {

    private String content;

    private Set<Integer> teamNumbers;

    @Override
    public String text() {
        return content;
    }

    @Override
    public MessageStore store() {
        MessageStore.ConfirmTeamMessageStore store = new MessageStore.ConfirmTeamMessageStore();
        store.setContent(content);
        store.setTeamNumbers(teamNumbers);
        store.setNumber(getNumber());
        return store;
    }

}
