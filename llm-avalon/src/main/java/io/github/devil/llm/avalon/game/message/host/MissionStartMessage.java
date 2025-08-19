package io.github.devil.llm.avalon.game.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.message.HostMessage;

import java.util.Map;

/**
 * @author Devil
 */
public class MissionStartMessage extends HostMessage {

    private final String textTemplate = """
        组队投票成功，请组队成员执行任务。
        """;

    public MissionStartMessage(String gameId) {
        super(gameId);
    }

    @Override
    public String text() {
        return new PromptTemplate(textTemplate).apply(
            Map.of()
        ).text();
    }

    @Override
    public String type() {
        return Type.MissionStartMessage;
    }

    @Override
    public MData data() {
        return null;
    }

}
