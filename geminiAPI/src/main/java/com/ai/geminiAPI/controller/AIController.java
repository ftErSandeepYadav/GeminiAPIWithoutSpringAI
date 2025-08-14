package com.ai.geminiAPI.controller;

import com.ai.geminiAPI.dto.GeminiRequest;
import com.ai.geminiAPI.dto.GeminiResponse;
import com.ai.geminiAPI.dto.TtsRequest;
import com.ai.geminiAPI.service.ChatAndImageSevice;
import com.ai.geminiAPI.service.TtsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class AIController {

    private final ChatAndImageSevice chatAndImageSevice;
    private final TtsService ttsService;

    public AIController(ChatAndImageSevice chatAndImageSevice, TtsService ttsService) {
        this.ttsService = ttsService ;
        this.chatAndImageSevice = chatAndImageSevice;
    }


    @PostMapping("/ask")
    public Mono<GeminiResponse> askQuestion(@RequestBody String question){
        return chatAndImageSevice.getAnswer(question) ;
    }

    @PostMapping("/generate-image")
    public Mono<GeminiResponse> generateImage(@RequestBody String prompt) {
        return chatAndImageSevice.generateImage(prompt);
    }

    @PostMapping("/generate-image-html")
    public Mono<ResponseEntity<String>> generateImageAsHtml(@RequestBody String prompt) {
        return chatAndImageSevice.generateImageAsHtml(prompt)
                .map(html -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html));
    }

    @PostMapping("/generate-speech")
    public Mono<GeminiResponse> generateSpeech(@RequestBody TtsRequest ttsRequest){
        return ttsService.generateSpeech(ttsRequest);
    }

    @PostMapping("/generate-speech-html")
    public Mono<ResponseEntity<String>> generateSpeechAsHtml(@RequestBody TtsRequest request) {
        return ttsService.generateSpeechAsHtml(request)
                .map(html -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html));
    }


}
