package com.demo.sblangchain.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "assistant.llm.ollama", name = "enabled", havingValue = "Y")
public class OllamaConfig {

    @Value("${assistant.llm.ollama.base-url}")
    private String baseUrl;

    @Value("${assistant.llm.ollama.model}")
    private String modelName;

    @Value("${assistant.llm.ollama.timeout}")
    private Duration timeout;

    @Value("${assistant.llm.ollama.max-retries}")
    private Integer maxRetries;

    @Bean
    ChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(timeout)
                .maxRetries(maxRetries)
                .build();
    }
}
