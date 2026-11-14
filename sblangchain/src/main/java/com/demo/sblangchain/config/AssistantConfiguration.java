package com.demo.sblangchain.config;

import com.demo.sblangchain.assistant.EmployeeAssistant;
import com.demo.sblangchain.tool.EmployeeTool;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "assistant.llm.gemini", name = "enabled", havingValue = "Y")
    EmployeeAssistant geminiAssistant(LlmProperties properties, EmployeeTool employeeTool) {
        requireSingleEnabledProvider(properties);
        LlmProperties.Provider gemini = properties.getGemini();
        requireApiKey(gemini, "Gemini");
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(gemini.getApiKey())
                .modelName(gemini.getModel())
                .build();
        return createAssistant(model, employeeTool);
    }

    @Bean
    @ConditionalOnProperty(prefix = "assistant.llm.groq", name = "enabled", havingValue = "Y")
    EmployeeAssistant groqAssistant(LlmProperties properties, EmployeeTool employeeTool) {
        requireSingleEnabledProvider(properties);
        LlmProperties.Provider groq = properties.getGroq();
        requireApiKey(groq, "Groq");
        ChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(groq.getApiKey())
                .modelName(groq.getModel())
                .build();
        return createAssistant(model, employeeTool);
    }

    @Bean
    @ConditionalOnProperty(prefix = "assistant.llm.ollama", name = "enabled", havingValue = "Y")
    EmployeeAssistant ollamaAssistant(
            ChatModel ollamaChatModel, LlmProperties properties, EmployeeTool employeeTool) {
        requireSingleEnabledProvider(properties);
        return createAssistant(ollamaChatModel, employeeTool);
    }

    private EmployeeAssistant createAssistant(ChatModel model, EmployeeTool employeeTool) {
        return AiServices.builder(EmployeeAssistant.class)
                .chatModel(model)
                .tools(employeeTool)
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }

    private void requireSingleEnabledProvider(LlmProperties properties) {
        long enabledCount = java.util.stream.Stream.of(
                        properties.getGemini(),
                        properties.getGroq(),
                        properties.getOllama())
                .filter(provider -> "Y".equalsIgnoreCase(provider.getEnabled()))
                .count();
        if (enabledCount > 1) {
            throw new IllegalStateException(
                    "Only one LLM provider may be enabled at a time. Set exactly one provider's enabled flag to Y.");
        }
    }

    private void requireApiKey(LlmProperties.Provider provider, String name) {
        if (provider.getApiKey() == null || provider.getApiKey().isBlank()) {
            throw new IllegalStateException(name + " is enabled but its API key is missing.");
        }
    }
}
