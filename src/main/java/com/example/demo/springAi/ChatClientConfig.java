package com.example.demo.springAi;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.ai.ollama.OllamaChatModel;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(new LoggingCallAdvisor()).build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        OllamaApi api = OllamaApi.builder()
                .baseUrl("http://localhost:11434")
                .build();

        OllamaOptions options = new OllamaOptions();
        options.setModel("mxbai-embed-large");
        options.setTemperature(0.1);         // 선택
        options.setFormat("json");            // 선택 (예: "json", "text")

        ObservationRegistry registry = ObservationRegistry.NOOP;

        ModelManagementOptions managementOptions = ModelManagementOptions.defaults();


        return new OllamaEmbeddingModel(api, options, registry, managementOptions);
    }
}
