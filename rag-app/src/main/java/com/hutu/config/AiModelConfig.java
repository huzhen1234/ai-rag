package com.hutu.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiModelConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, VectorStore pgVectorStore) {
        return ChatClient
                .builder(ollamaChatModel)
                // 添加了这样的配置就不需要 OllamaController askQuestion "/rag/ask" 写这么复杂的接口了。
                .defaultAdvisors(new QuestionAnswerAdvisor(pgVectorStore))
                .build();
    }
}
