package io.github.devil.llm.avalon.llm;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiLanguageModel;

/**
 * @author Devil
 */
public class Common {

    public static ChatModel chatModel() {
        return OpenAiChatModel.builder()
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("qwen-plus-2025-07-14")
            .temperature(0.0)
            .responseFormat("{\"type\": \"json_object\"}")
            .logRequests(true)
            .logResponses(true)
            .build();
    }

    public static OpenAiLanguageModel streamModel() {
        return OpenAiLanguageModel.builder()
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("qwen-plus-latest")
            .temperature(0.0)
            .logRequests(true)
            .logResponses(true)
            .build();
    }
}
