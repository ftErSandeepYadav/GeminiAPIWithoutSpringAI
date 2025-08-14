package com.ai.geminiAPI.service;

import com.ai.geminiAPI.dto.GeminiRequest;
import com.ai.geminiAPI.dto.GeminiResponse;
import com.ai.geminiAPI.dto.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@Service
public class ChatAndImageSevice {

    private final WebClient webClient ;
    public ChatAndImageSevice(WebClient.Builder webClientBuilder) {
        // Configure the buffer size to handle large image data responses.
        final int bufferSize = 16 * 1024 * 1024; // 16 MB

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs
                        .defaultCodecs()
                        .maxInMemorySize(bufferSize))
                .build();

        this.webClient = webClientBuilder
                .exchangeStrategies(strategies)
                .build();
    }

    // Access to API key and url
    @Value("${gemini.api.text.url}")
    private String textApiUrl;
    @Value("${gemini.api.key}")
    private String apiKey ;

    @Value("${gemini.api.image.url}")
    private String imageApiUrl ;

    public Mono<GeminiResponse> getAnswer(String prompt) {

        //Construct the request payload
        GeminiRequest geminiRequest = createTextRequest(prompt);

        // Construct the full URL for the API call.
        String fullUrl = textApiUrl + "?key=" + apiKey;

        return callGeminiApi(fullUrl, geminiRequest);
    }

    public Mono<GeminiResponse> generateImage(String prompt) {
        // Create the request body for image generation.
        GeminiRequest geminiRequest = createImageRequest(prompt);

        // Construct the full URL for the API call.
        String fullUrl = imageApiUrl + "?key=" + apiKey;

        return callGeminiApi(fullUrl, geminiRequest);
    }

    public Mono<String> generateImageAsHtml(String prompt) {
        GeminiRequest geminiRequest = createImageRequest(prompt);
        String fullUrl = imageApiUrl + "?key=" + apiKey;

        return callGeminiApi(fullUrl, geminiRequest)
                .map(geminiResponse -> {
                    // Extract the base64 data from the response.
                    String base64Data = geminiResponse.getCandidates().get(0)
                            .getContent().getParts().stream()
                            .filter(part -> part.getInlineData() != null)
                            .findFirst()
                            .map(part -> part.getInlineData().getData())
                            .orElse("");

                    if (base64Data.isEmpty()) {
                        return "<html><body><h1>Error: Could not retrieve image data from API response.</h1></body></html>";
                    }

                    // Create a self-contained HTML string with some basic styling.
                    return "<!DOCTYPE html>" +
                            "<html>" +
                            "<head><title>Generated Image</title></head>" +
                            "<body style='margin:0; display:flex; justify-content:center; align-items:center; height:100vh; background-color:#f0f0f0;'>" +
                            "<img src='data:image/png;base64," + base64Data + "' alt='Generated Image' style='max-width:90%; max-height:90%; border:1px solid #ccc; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>" +
                            "</body>" +
                            "</html>";
                });
    }

    private GeminiRequest createTextRequest(String prompt) {
        GeminiRequest request = new GeminiRequest();
        Part part = new Part();
        part.setText(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content();
        content.setParts(Collections.singletonList(part));
        request.setContents(Collections.singletonList(content));
        return request;
    }

    private GeminiRequest createImageRequest(String prompt) {
        GeminiRequest request = createTextRequest(prompt); // Start with a basic text request
        GeminiRequest.GenerationConfig generationConfig = new GeminiRequest.GenerationConfig();
        generationConfig.setResponseModalities(Arrays.asList("TEXT", "IMAGE"));
        request.setGenerationConfig(generationConfig);
        return request;
    }

    private Mono<GeminiResponse> callGeminiApi(String url, GeminiRequest request) {
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .doOnError(error -> {
                    // Simple error logging
                    System.err.println("Error calling Gemini API: " + error.getMessage());
                });
    }

}
