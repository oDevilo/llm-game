package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
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
    
}