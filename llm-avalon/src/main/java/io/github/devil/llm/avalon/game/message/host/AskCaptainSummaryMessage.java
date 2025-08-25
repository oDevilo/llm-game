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

    public AskCaptainSummaryMessage(String gameId, Integer round, Integer turn) {
        super(gameId, round, turn);
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