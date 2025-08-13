package io.github.devil.llm.avalon.game.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.message.HostMessage;

import java.util.Map;

/**
 * @author Devil
 */
public class AskSpeakMessage extends HostMessage {

    private final int number;

    private final String textTemplate = """
        下面请{{number}}号玩家发言
        """;

    // PlayerChatMessage
    private final String promptTemplate = """
        下面请{{number}}号玩家发言

        # 返回格式
        {
            "thinking": "思考过程",
            "speak": "发言内容",
        }
        """;

    public AskSpeakMessage(int number) {
        this.number = number;
    }

    @Override
    public String prompt() {
        return new PromptTemplate(promptTemplate).apply(
            Map.of(
                "number", number
            )
        ).text();
    }

    @Override
    public String text() {
        return new PromptTemplate(textTemplate).apply(
            Map.of(
                "number", number
            )
        ).text();
    }

}