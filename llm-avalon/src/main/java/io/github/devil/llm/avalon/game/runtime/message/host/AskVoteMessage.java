package io.github.devil.llm.avalon.game.runtime.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.store.MessageStore;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Devil
 */
public class AskVoteMessage extends HostMessage {

    private final Set<Integer> team;

    private final String textTemplate = """
        当前队伍由{{numbers}}号玩家组成，请所有人对当前车队进行投票
        """;

    // VoteMessage
    private final String promptTemplate = """
        当前队伍由{{numbers}}号玩家组成，请结合你的身份对当前车队进行投票
        
        # 返回格式
        {
            "agree": "是否同意，可以返回`true`或者`false`"
        }
        
        # 返回示例
        {
            "agree": false
        }
        """;

    public AskVoteMessage(Set<Integer> team) {
        this.team = team;
    }

    @Override
    public String prompt() {
        return new PromptTemplate(promptTemplate).apply(
            Map.of(
                "numbers", team.stream().map(String::valueOf).collect(Collectors.joining("、"))
            )
        ).text();
    }

    @Override
    public String text() {
        return new PromptTemplate(textTemplate).apply(
            Map.of(
                "numbers", team.stream().map(String::valueOf).collect(Collectors.joining("、"))
            )
        ).text();
    }

    @Override
    public MessageStore store() {
        MessageStore.AskVoteMessageStore store = new MessageStore.AskVoteMessageStore();
        store.setTeam(team);
        return store;
    }

}
