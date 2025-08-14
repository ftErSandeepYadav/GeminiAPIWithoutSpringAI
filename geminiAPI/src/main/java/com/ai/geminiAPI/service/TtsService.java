package com.ai.geminiAPI.service;

import com.ai.geminiAPI.dto.GeminiResponse;
import com.ai.geminiAPI.dto.TtsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

/**
 * Service class to interact with the Google Gemini Text-to-Speech API.
 */
@Service
public class TtsService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.tts.url}")
    private String ttsApiUrl;

    public TtsService(WebClient.Builder webClientBuilder) {
        // Configure the buffer size to handle audio data responses.
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

    /**
     * Calls the Gemini API to generate speech and returns it embedded in an HTML string.
     *
     * @param ttsRequest The request object containing the text and voice configuration.
     * @return A Mono containing a full HTML document with the generated audio.
     */
    public Mono<String> generateSpeechAsHtml(TtsRequest ttsRequest) {
        return generateSpeech(ttsRequest)
                .map(geminiResponse -> {
                    String pcmBase64Data = geminiResponse.getCandidates().get(0)
                            .getContent().getParts().stream()
                            .filter(part -> part.getInlineData() != null)
                            .findFirst()
                            .map(part -> part.getInlineData().getData())
                            .orElse("");

                    if (pcmBase64Data.isEmpty()) {
                        return "<html><body><h1>Error: Could not retrieve audio data from API response.</h1></body></html>";
                    }

                    try {
                        // Decode the base64 PCM data and add a WAV header.
                        byte[] pcmData = Base64.getDecoder().decode(pcmBase64Data);
                        byte[] wavData = addWavHeader(pcmData);
                        String wavBase64Data = Base64.getEncoder().encodeToString(wavData);

                        // Create a self-contained HTML string with an audio player.
                        return "<!DOCTYPE html>" +
                                "<html>" +
                                "<head><title>Generated Speech</title></head>" +
                                "<body style='margin:0; display:flex; flex-direction:column; justify-content:center; align-items:center; height:100vh; background-color:#f0f0f0; font-family: sans-serif;'>" +
                                "<h2>Generated Audio</h2>" +
                                "<audio controls autoplay>" +
                                "<source src='data:audio/wav;base64," + wavBase64Data + "' type='audio/wav'>" +
                                "Your browser does not support the audio element." +
                                "</audio>" +
                                "</body>" +
                                "</html>";
                    } catch (IOException e) {
                        // Log the exception properly in a real application
                        e.printStackTrace();
                        return "<html><body><h1>Error: Failed to process audio data.</h1></body></html>";
                    }
                });
    }

    public Mono<GeminiResponse> generateSpeech(TtsRequest ttsRequest) {
        String fullUrl = ttsApiUrl + "?key=" + apiKey;

        return webClient.post()
                .uri(fullUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(ttsRequest)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .doOnError(error -> {
                    System.err.println("Error calling Gemini TTS API: " + error.getMessage());
                });
    }

    /**
     * Prepends a 44-byte WAV file header to raw PCM audio data.
     *
     * @param pcmData The raw PCM audio data (16-bit little-endian).
     * @return A byte array containing the complete WAV file data.
     * @throws IOException If an I/O error occurs.
     */
    private byte[] addWavHeader(byte[] pcmData) throws IOException {
        long sampleRate = 24000; // As specified by the API documentation
        int numChannels = 1;
        int bitsPerSample = 16;
        long byteRate = sampleRate * numChannels * bitsPerSample / 8;
        int blockAlign = numChannels * bitsPerSample / 8;
        long subChunk2Size = pcmData.length;
        long chunkSize = 36 + subChunk2Size;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(44);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // RIFF chunk descriptor
        buffer.put("RIFF".getBytes());
        buffer.putInt((int) chunkSize);
        buffer.put("WAVE".getBytes());

        // "fmt " sub-chunk
        buffer.put("fmt ".getBytes());
        buffer.putInt(16); // Subchunk1Size for PCM
        buffer.putShort((short) 1); // AudioFormat 1 for PCM
        buffer.putShort((short) numChannels);
        buffer.putInt((int) sampleRate);
        buffer.putInt((int) byteRate);
        buffer.putShort((short) blockAlign);
        buffer.putShort((short) bitsPerSample);

        // "data" sub-chunk
        buffer.put("data".getBytes());
        buffer.putInt((int) subChunk2Size);

        out.write(buffer.array());
        out.write(pcmData);
        out.close();

        return out.toByteArray();
    }
}
