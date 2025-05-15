package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class EmbeddingController {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private EmbeddingModel embeddingModel;

    @PostConstruct
    public void init() {
        List<Document> docs = List.of(
                new Document("강아지와 함께 여유로운 시간을 보낼 수 있는 감성적인 분위기의 카페입니다. 야외 테라스와 펫 전용 식기가 준비되어 있어 반려동물과 방문하기 좋습니다.", Map.of("name", "멍멍카페")),
                new Document("자연광이 가득 들어오는 대형 창과 푸르른 식물들로 꾸며져 있어 힐링하기 좋은 포레스트 테마의 카페입니다. 조용한 분위기에서 대화를 나누기 좋습니다.", Map.of("name", "포레스트 커피")),
                new Document("조용한 음악과 함께 책을 읽거나 사색하기 좋은 북카페로, 아늑한 분위기와 부드러운 조명이 특징입니다. 혼자만의 시간을 보내기 적합합니다.", Map.of("name", "책방커피")),
                new Document("수성못이 한눈에 보이는 탁 트인 테라스가 인상적인 뷰 카페입니다. 햇살 좋은 날 야외에서 커피를 마시며 경치를 감상하기 좋습니다.", Map.of("name", "카페 루프")),
                new Document("카페 내부가 예술 작품으로 가득 채워져 있는 갤러리형 감성 카페입니다. 예술 전시를 감상하면서 커피를 즐길 수 있는 독특한 공간입니다.", Map.of("name", "아트앤커피")),
                new Document("정성스럽게 만든 수제 디저트와 다양한 음료가 준비된 조용한 분위기의 디저트 전문 카페입니다. 커플이나 친구끼리 달콤한 시간을 보내기 좋습니다.", Map.of("name", "베이크숍 수성")),
                new Document("부드러운 햇살이 들어오는 창가 자리에 앉아 따뜻한 커피를 즐길 수 있는 감성 카페입니다. 여유로운 분위기로 데이트 장소로도 좋습니다.", Map.of("name", "선샤인 커피")),
                new Document("깔끔하고 미니멀한 인테리어가 돋보이는 브루잉 전문 카페입니다. 커피 본연의 맛을 제대로 즐길 수 있도록 다양한 원두를 제공합니다.", Map.of("name", "브루웍스")),
                new Document("감성적인 조명과 멋진 야경이 어우러진 밤 분위기의 카페입니다. 연인과 함께 로맨틱한 분위기를 즐기기 좋은 공간입니다.", Map.of("name", "문라이트 커피")),
                new Document("넓은 주차 공간과 쾌적한 실내 환경이 마련된 가족 단위 방문자에게 적합한 패밀리 카페입니다. 아이들과 함께 오기 좋습니다.", Map.of("name", "드라이브 커피")),
                new Document("직접 로스팅한 원두로 내려주는 신선한 커피를 제공하는 로스터리 카페입니다. 커피 애호가들을 위한 진지한 커피 경험을 제공합니다.", Map.of("name", "빈스 로스터리")),
                new Document("식물 인테리어로 꾸며져 자연을 닮은 공간에서 힐링할 수 있는 카페입니다. 녹음이 가득한 분위기에서 머물기 좋습니다.", Map.of("name", "그린포레스트")),
                new Document("꽃향기와 플로럴 디자인이 어우러져 마치 정원에 온 듯한 분위기의 카페입니다. 사진 찍기 좋은 감성 포토존도 많습니다.", Map.of("name", "플라워빈")),
                new Document("대형 서점과 연결되어 있는 북카페로, 책을 읽으며 커피를 마실 수 있는 복합문화 공간입니다. 조용한 학습 및 휴식 공간으로 적합합니다.", Map.of("name", "페이지터너")),
                new Document("노트북 작업이나 공부를 위한 전용 좌석과 콘센트가 마련된 조용한 분위기의 워크 카페입니다. 프리랜서나 학생들에게 인기가 많습니다.", Map.of("name", "워크카페")),
                new Document("소규모 공연과 문화 이벤트가 열리는 문화복합형 카페입니다. 아티스트들의 무대와 함께 커피를 즐길 수 있는 공간입니다.", Map.of("name", "소리숲")),
                new Document("아침 식사 메뉴와 브런치를 제공하는 카페로, 건강한 식단과 함께 여유로운 하루를 시작할 수 있는 장소입니다.", Map.of("name", "모닝테이블")),
                new Document("레트로 스타일의 인테리어와 음악이 흐르는 복고풍 카페입니다. 옛 감성을 느끼고 싶은 이들에게 추천하는 공간입니다.", Map.of("name", "다방1930")),
                new Document("다양한 허브티와 과일티를 전문적으로 제공하는 티 카페입니다. 조용하고 차분한 분위기에서 휴식을 취하기 좋습니다.", Map.of("name", "티앤라운지")),
                new Document("수성못 인근에 위치한 조용한 분위기의 베이커리 카페입니다. 고요한 오후 시간을 보내기에 적절한 힐링 공간입니다.", Map.of("name", "카페 휴심"))
        );
        vectorStore.add(docs);

        System.out.println("=== 사용 중인 임베딩 모델 구현체 ===");
        System.out.println("embeddingModel 구현체: " + embeddingModel.getClass().getName());

        System.out.println("=== 문서 임베딩 벡터 출력 ===");
        docs.forEach(doc -> {
            float[] vector = embeddingModel.embed(Objects.requireNonNull(doc.getText()));
            System.out.println("벡터 크기: " + vector.length);
            System.out.println("벡터 일부: " + Arrays.toString(Arrays.copyOfRange(vector, 0, Math.min(5, vector.length))) + " ...");
        });
    }

    @GetMapping("/embedding")
    public String recommend(@RequestParam String question) {
        float[] questionVector = embeddingModel.embed(question);
        System.out.println("=== 질문 임베딩 벡터 ===");
        System.out.println("벡터 크기: " + questionVector.length);
        System.out.println("벡터 일부: " + Arrays.toString(Arrays.copyOfRange(questionVector, 0, 5)) + " ...");

        List<Document> docs = Objects.requireNonNull(vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(3).similarityThreshold(0.5).build()
        )).stream().distinct().toList();

        docs.forEach(doc -> {
            System.out.println("문서: " + doc.getText());
            System.out.println("메타데이터: " + doc.getMetadata());
        });

        String context = docs.stream()
                .map(doc -> "- 이름: " + doc.getMetadata().get("name") + ", 설명: " + doc.getText())
                .collect(Collectors.joining("\n"));

        // 로그 출력 추가
        System.out.println("검색된 문서:");
        docs.forEach(doc -> System.out.println(doc.getText()));

        System.out.println("LLM 전달 context:");
        System.out.println(context);

        return chatClient.prompt()
                .system("다음 카페 정보 목록을 기반으로 정확하고 구체적으로 질문에 답변해줘. 아래 목록에 없는 카페는 절대 언급하지 마:\n" + context)
                .user(question)
                .call()
                .content();
    }
}
