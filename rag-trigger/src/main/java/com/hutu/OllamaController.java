package com.hutu;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/ollama/")
@RequiredArgsConstructor
public class OllamaController implements IAiService {

    @Value("classpath:/data/ragTest.txt")
    private Resource springAiResource;

    private final VectorStore vectorStore;

    private final ChatClient chatClient;

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

    @GetMapping("/rag/importDocument")
    @Override
    public void uploadFile() {
        DocumentReader reader = new TikaDocumentReader(springAiResource);
        List<Document> documents = reader.get();
        // TODO split trunks 防止文档过大
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
        vectorStore.add(splitDocuments);
    }

    @GetMapping("/rag/importDocument/tag")
    public void uploadFileTag() {
        DocumentReader reader = new TikaDocumentReader(springAiResource);
        List<Document> documents = reader.get();
        // TODO split trunks 防止文档过大
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
        splitDocuments.forEach(document -> document.getMetadata().put("namespace","TEST"));
        vectorStore.add(splitDocuments);
    }

    @GetMapping("/rag/ask")
    public String askQuestion(@RequestParam("question") String question) {
        // 检索相关文档
        List<Document> similarDocuments = vectorStore.similaritySearch(question);

        if (similarDocuments.isEmpty()) {
            return "未找到相关文档内容，请先导入文档。";
        }

        // 构建上下文
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        // 构建提示词
        String promptTemplate = """
            请基于以下文档内容回答问题。如果文档内容不包含相关信息，请说明无法基于提供的文档回答该问题。
            
            文档内容：
            {context}
            
            问题：{question}
            
            回答：
            """;

        String finalPrompt = promptTemplate.replace("{context}", context)
                .replace("{question}", question);

        // 调用模型生成回答
        return chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();
    }

    @GetMapping("/rag/ask/tag")
    public String askQuestionTag(@RequestParam("question") String question) {
        // 检索相关文档
        SearchRequest searchRequest = SearchRequest.builder().query(question).topK(4).filterExpression("name == 'xxx'").build();

        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        if (similarDocuments.isEmpty()) {
            return "未找到相关文档内容，请先导入文档。";
        }

        // 构建上下文
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        // 构建提示词
        String promptTemplate = """
            请基于以下文档内容回答问题。如果文档内容不包含相关信息，请说明无法基于提供的文档回答该问题。
            
            文档内容：
            {context}
            
            问题：{question}
            
            回答：
            """;

        String finalPrompt = promptTemplate.replace("{context}", context)
                .replace("{question}", question);

        // 调用模型生成回答
        return chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();
    }


}
