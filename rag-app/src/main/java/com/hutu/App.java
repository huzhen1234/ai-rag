package com.hutu;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class App {

    @Resource
    private OllamaChatModel ollamaChatModel;

    @Resource
    private ChatClient chatClient;

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
