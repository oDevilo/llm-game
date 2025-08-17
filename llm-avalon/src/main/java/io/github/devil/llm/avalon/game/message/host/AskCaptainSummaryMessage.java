package io.github.devil.llm.avalon.game.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.message.HostMessage;

import java.util.Map;

/**
 * @author Devil
 */
public class AskCaptainSummaryMessage extends HostMessage {

    private final String textTemplate = """
        请队长进行总结发言，并请确认最终车队人员
        """;

    // ConfirmTeamMessage
    private final String promptTemplate = """
        请队长进行总结发言，并请确认最终车队人员
        
        # 返回格式
        {
            "content": "发言内容"
            "teamNumbers": [1, 2, 3]
        }
        """;

    public AskCaptainSummaryMessage(String gameId) {
        super(gameId);
    }

    @Override
    public String prompt() {
        return new PromptTemplate(promptTemplate).apply(
            Map.of(
            )
        ).text();
    }

    @Override
    public String text() {
        return new PromptTemplate(textTemplate).apply(
            Map.of(
            )
        ).text();
    }

    @Override
    public String type() {
        return Type.AskCaptainSummaryMessage;
    }

    @Override
    public MData data() {
        return null;
    }

}