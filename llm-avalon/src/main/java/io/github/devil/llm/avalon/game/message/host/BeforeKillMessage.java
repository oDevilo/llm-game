package io.github.devil.llm.avalon.game.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.message.HostMessage;

import java.util.Map;

/**
 * @author Devil
 */
public class BeforeKillMessage extends HostMessage {

    private final String textTemplate = """
        蓝方完成任务，进入刺杀梅林环节，请红方进行讨论选择要刺杀的对象。
        """;

    private final String promptTemplate = """
        蓝方完成任务，进入刺杀梅林环节，请红方进行讨论选择要刺杀的对象。
        """;

    public BeforeKillMessage(String gameId) {
        super(gameId);
    }

    public String prompt() {
        return new PromptTemplate(promptTemplate).apply(
            Map.of()
        ).text();
    }

    @Override
    public String text() {
        return new PromptTemplate(textTemplate).apply(
            Map.of()
        ).text();
    }

    @Override
    public String type() {
        return Type.BeforeKillMessage;
    }

    @Override
    public MData data() {
        return null;
    }

}