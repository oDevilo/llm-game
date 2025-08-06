package io.github.devil.llm.avalon.game.runtime.message.host;

import dev.langchain4j.model.input.PromptTemplate;
import io.github.devil.llm.avalon.game.store.MessageStore;

import java.util.Map;

/**
 * @author Devil
 */
public class AskKillMessage extends HostMessage {

    private final String textTemplate = """
        请红方刺客选择刺杀对象。
        """;

    private final String promptTemplate = """
        请红方刺客选择刺杀对象。
        
        # 返回格式
        {
            "killNumber": "想要刺杀的玩家号码"
        }
        
        # 返回示例
        {
            "killNumber": 1
        }
        """;

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
    public MessageStore store() {
        MessageStore.AskKillMessageStore store = new MessageStore.AskKillMessageStore();
        return store;
    }

}
