package io.github.devil.llm.avalon.game.runtime.message.player;

import io.github.devil.llm.avalon.game.store.MessageStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Devil
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DraftTeamMessage extends PlayerMessage {
    /**
     * 主要内容
     */
    private String content;
    /**
     * 确定的发言顺序
     */
    private String speakOrder;

    @Override
    public String text() {
        return content;
    }

    @Override
    public MessageStore store() {
        MessageStore.DraftTeamMessageStore store = new MessageStore.DraftTeamMessageStore();
        store.setContent(content);
        store.setSpeakOrder(speakOrder);
        store.setNumber(getNumber());
        return store;
    }
}
