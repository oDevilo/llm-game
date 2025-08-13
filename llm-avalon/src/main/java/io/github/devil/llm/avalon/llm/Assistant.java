package io.github.devil.llm.avalon.llm;

import dev.langchain4j.service.Result;

public interface Assistant {
    Result<String> chat(String userMessage);
}