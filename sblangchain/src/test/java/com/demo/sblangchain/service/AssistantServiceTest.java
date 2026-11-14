package com.demo.sblangchain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.demo.sblangchain.assistant.EmployeeAssistant;
import com.demo.sblangchain.dto.ChatRequest;
import dev.langchain4j.exception.TimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.server.ResponseStatusException;

class AssistantServiceTest {

    @Test
    void mapsModelTimeoutToGatewayTimeout() {
        EmployeeAssistant assistant = mock(EmployeeAssistant.class);
        when(assistant.chat("session", "question")).thenThrow(new TimeoutException("request timed out"));
        ObjectProvider<EmployeeAssistant> assistantProvider = mock(ObjectProvider.class);
        when(assistantProvider.getIfAvailable()).thenReturn(assistant);

        AssistantService service = new AssistantService(assistantProvider);

        assertThatThrownBy(() -> service.chat(new ChatRequest("session", "question")))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode().value()).isEqualTo(504);
                    assertThat(exception.getReason()).contains("configured retries");
                });
    }
}
