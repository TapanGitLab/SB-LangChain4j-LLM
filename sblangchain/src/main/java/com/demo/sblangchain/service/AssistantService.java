package com.demo.sblangchain.service;

import com.demo.sblangchain.assistant.EmployeeAssistant;
import com.demo.sblangchain.dto.ChatRequest;
import com.demo.sblangchain.dto.ChatResponse;
import dev.langchain4j.exception.TimeoutException;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssistantService {

    private final ObjectProvider<EmployeeAssistant> assistantProvider;

    public AssistantService(ObjectProvider<EmployeeAssistant> assistantProvider) {
        this.assistantProvider = assistantProvider;
    }

    public ChatResponse chat(ChatRequest request) {
        EmployeeAssistant assistant = assistantProvider.getIfAvailable();
        if (assistant == null) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No LLM provider is enabled. Enable one provider in application.properties.");
        }
        String sessionId = request.sessionId() == null || request.sessionId().isBlank()
                ? UUID.randomUUID().toString()
                : request.sessionId();
        try {
            return new ChatResponse(sessionId, assistant.chat(sessionId, request.message()));
        } catch (TimeoutException exception) {
            throw new ResponseStatusException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "The language model timed out after its configured retries. Try the request again; "
                            + "if this keeps happening, check that Ollama is responsive and increase "
                            + "assistant.llm.ollama.timeout.",
                    exception);
        }
    }
}
