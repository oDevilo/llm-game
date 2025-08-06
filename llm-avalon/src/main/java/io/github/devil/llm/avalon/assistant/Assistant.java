package io.github.devil.llm.avalon.assistant;

import dev.langchain4j.service.Result;

public interface Assistant {
    Result<String> chat(String userMessage);
}