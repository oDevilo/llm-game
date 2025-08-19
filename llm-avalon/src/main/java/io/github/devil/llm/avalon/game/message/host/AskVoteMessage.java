package io.github.devil.llm.avalon.game.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.message.HostMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
public class AskVoteMessage extends HostMessage {

    private final MessageData data;

    private final String textTemplate = """
        当前队伍由{{numbers}}号玩家组成，请所有人对当前车队进行投票
        """;

    public AskVoteMessage(String gameId, MessageData data) {
        super(gameId);
        this.data = data;
    }

    @Override
    public String text() {
        return new PromptTemplate(textTemplate).apply(
            Map.of(
                "numbers", data.team.stream().map(String::valueOf).collect(Collectors.joining("、"))
            )
        ).text();
    }

    @Override
    public String type() {
        return Type.AskVoteMessage;
    }

    @Override
    public MData data() {
        return data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageData implements MData {
        private Set<Integer> team;
    }

}