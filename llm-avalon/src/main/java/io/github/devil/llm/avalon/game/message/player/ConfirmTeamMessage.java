package io.github.devil.llm.avalon.game.message.player;

import io.github.devil.llm.avalon.game.message.PlayerMessage;
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

}