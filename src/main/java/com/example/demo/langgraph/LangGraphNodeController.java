package com.example.demo.langgraph;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class LangGraphNodeController {

    @Autowired
    private VectorStore vectorStore;
    @Autowired private ChatClient chatClient;

    @PostMapping("/search")
    public List<DocumentsRequest.CafeInfo> search(@RequestBody QuestionRequest req) {
        List<Document> docs = Objects.requireNonNull(vectorStore.similaritySearch(
                SearchRequest.builder().query(req.getQuestion()).topK(3).similarityThreshold(0.5).build()
        )).stream().distinct().toList();

        return docs.stream().map(doc -> {
            DocumentsRequest.CafeInfo info = new DocumentsRequest.CafeInfo();
            info.setName(String.valueOf(doc.getMetadata().get("name")));
            info.setText(doc.getText());
            return info;
        }).toList();
    }

    @PostMapping("/summarize")
    public String summarize(@RequestBody DocumentsRequest req) {
        String context = req.getDocuments().stream()
                .map(doc -> "- 이름: " + doc.getName() + ", 설명: " + doc.getText())
                .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system("다음 문서를 요약해줘:\n" + context)
                .user("요약해줘")
                .call()
                .content();
    }

    @PostMapping("/answer")
    public String answer(@RequestBody AnswerRequest req) {
        return chatClient.prompt()
                .system("다음 카페 정보 목록을 기반으로 정확하고 구체적으로 질문에 답변해줘. 아래 목록에 없는 카페는 절대 언급하지 마:\n" + req.getSummary())
                .user(req.getQuestion())
                .call()
                .content();
    }
}
