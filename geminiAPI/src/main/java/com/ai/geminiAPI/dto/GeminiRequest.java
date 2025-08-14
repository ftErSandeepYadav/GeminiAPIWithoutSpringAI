package com.ai.geminiAPI.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Represents the request payload for the Gemini API.
 * Includes optional generationConfig for specific tasks like image generation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Prevents null fields from being serialized
public class GeminiRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }

    public void setGenerationConfig(GenerationConfig generationConfig) {
        this.generationConfig = generationConfig;
    }

    public static class Content {
        private List<Part> parts;

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class GenerationConfig {
        private List<String> responseModalities;

        public List<String> getResponseModalities() {
            return responseModalities;
        }

        public void setResponseModalities(List<String> responseModalities) {
            this.responseModalities = responseModalities;
        }
    }
}