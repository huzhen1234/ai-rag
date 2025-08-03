package com.hutu;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/ollama/")
public class OllamaController implements IAiService {

    @Resource
    private ChatClient chatClient;

    @GetMapping(value = "generate")
    @Override
    public String generate(@RequestParam("message") String message) {
        return chatClient.prompt().user(message).call().content();
    }


    @GetMapping(value = "generate_stream",produces = "text/html;charset=utf-8")
    @Override
    public Flux<String> generateStream(@RequestParam("message") String message) {
        return chatClient.prompt().user(message).stream().content();
    }

}
