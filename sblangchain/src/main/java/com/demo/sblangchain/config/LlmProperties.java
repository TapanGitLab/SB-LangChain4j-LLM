package com.demo.sblangchain.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "assistant.llm")
public class LlmProperties {

    @Valid
    private Provider gemini = new Provider();

    @Valid
    private Provider groq = new Provider();

    @Valid
    private OllamaProvider ollama = new OllamaProvider();

    public Provider getGemini() {
        return gemini;
    }

    public void setGemini(Provider gemini) {
        this.gemini = gemini;
    }

    public Provider getGroq() {
        return groq;
    }

    public void setGroq(Provider groq) {
        this.groq = groq;
    }

    public OllamaProvider getOllama() {
        return ollama;
    }

    public void setOllama(OllamaProvider ollama) {
        this.ollama = ollama;
    }

    public static class Provider {
        @Pattern(regexp = "[YyNn]", message = "must be Y or N")
        private String enabled = "N";
        private String apiKey = "";
        private String baseUrl = "";
        private String model = "";

        public String getEnabled() {
            return enabled;
        }

        public void setEnabled(String enabled) {
            this.enabled = enabled;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public static class OllamaProvider extends Provider {
        @NotNull
        private Duration timeout = Duration.ofMinutes(5);

        @Min(0)
        @Max(5)
        private Integer maxRetries = 5;

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }

        public Integer getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
        }
    }
}
