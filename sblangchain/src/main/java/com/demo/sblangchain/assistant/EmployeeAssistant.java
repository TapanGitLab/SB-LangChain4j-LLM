package com.demo.sblangchain.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface EmployeeAssistant {

    @SystemMessage("""
            You are a helpful employee assistant. Use the provided tools for all employee data,
            department counts, salary comparisons, and salary changes. Never invent employee
            details or claim a change succeeded unless a tool reports success. Ask a brief
            follow-up question if required information is missing. Do not expose implementation
            details, credentials, or database internals.
            """)
    String chat(@MemoryId String sessionId, @UserMessage String message);
}
